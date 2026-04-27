package at.abcdef.memmaster.service;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.repository.DictionaryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class DictionaryService {

  public record ExportFile(String filename, String contentType, byte[] content) {}

  private final DictionaryRepository dictionaryRepository;
  private final UserService userService;

  public DictionaryService(DictionaryRepository dictionaryRepository, UserService userService) {
    this.dictionaryRepository = dictionaryRepository;
    this.userService = userService;
  }

  public List<Dictionary> getDictionaryInFolder(@Valid Folder folder) {
    return dictionaryRepository.getByFolders(Collections.singletonList(folder));
  }

  public List<Dictionary> saveDictionaryInFolder(Folder folder, List<Dictionary> dto) {

    List<Dictionary> existingRecords = dto.stream().filter(d -> d.getId() != null).toList();
    List<Long> existingIds = existingRecords.stream().map(Dictionary::getId).toList();

    dictionaryRepository.deleteAllByFoldersContainingAndIdNotIn(folder, existingIds);

    for (Dictionary d : dto) {
      if (d.getFolders() == null) {
        d.setFolders(new ArrayList<>());
      }
      if (!d.getFolders().contains(folder)) {
        d.getFolders().add(folder);
      }
      d.setUser(userService.getCurrentUser());
    }
    dictionaryRepository.saveAll(dto);
    return dto;
  }

  public List<Dictionary> bulkImportDictionary(Folder folder, List<Dictionary> newPairs) {
    Set<String> existingKeys = new HashSet<>();
    for (Dictionary existing : getDictionaryInFolder(folder)) {
      String normalized = normalizeKey(existing.getName());
      if (normalized != null) {
        existingKeys.add(normalized);
      }
    }

    List<Dictionary> toSave = new ArrayList<>();
    for (Dictionary pair : newPairs) {
      String normalized = normalizeKey(pair.getName());
      if (normalized == null || existingKeys.contains(normalized)) {
        continue;
      }
      existingKeys.add(normalized);
      prepareForFolderSave(pair, folder);
      toSave.add(pair);
    }

    return toSave.isEmpty() ? List.of() : dictionaryRepository.saveAll(toSave);
  }

  public List<Dictionary> bulkImportDictionaryFromFile(Folder folder, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return List.of();
    }

    String originalFileName = file.getOriginalFilename();
    if (originalFileName == null) {
      throw new IllegalArgumentException("Unsupported file format");
    }

    String lowerName = originalFileName.toLowerCase(Locale.ROOT);
    List<Dictionary> parsedPairs;
    try {
      if (lowerName.endsWith(".csv")) {
        parsedPairs = parseCsv(file);
      } else if (lowerName.endsWith(".xlsx") || lowerName.endsWith(".xls")) {
        parsedPairs = parseWorkbook(file);
      } else {
        throw new IllegalArgumentException("Unsupported file format");
      }
    } catch (IOException e) {
      throw new IllegalStateException("Cannot parse import file", e);
    }

    return bulkImportDictionary(folder, parsedPairs);
  }

  public ExportFile exportDictionary(Folder folder, String format) {
    String normalizedFormat = (format == null ? "csv" : format).toLowerCase(Locale.ROOT);
    List<Dictionary> cards = getDictionaryInFolder(folder);
    String baseName = safeFileNamePart(folder.getName());

    try {
      return switch (normalizedFormat) {
        case "xlsx", "excel" -> new ExportFile(
          baseName + ".xlsx",
          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          toXlsx(cards)
        );
        case "docx", "word" -> new ExportFile(
          baseName + ".docx",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
          toDocx(cards)
        );
        case "csv" -> new ExportFile(
          baseName + ".csv",
          "text/csv; charset=UTF-8",
          toCsv(cards)
        );
        default -> throw new IllegalArgumentException("Unsupported export format: " + format);
      };
    } catch (IOException e) {
      throw new IllegalStateException("Failed to generate export", e);
    }
  }

  private List<Dictionary> parseCsv(MultipartFile file) throws IOException {
    List<Dictionary> parsed = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) {
          continue;
        }
        List<String> cells = parseCsvLine(line);
        if (cells.size() < 2) {
          continue;
        }
        parsed.add(newDictionary(cells.get(0), cells.get(1)));
      }
    }
    return parsed;
  }

  private List<Dictionary> parseWorkbook(MultipartFile file) throws IOException {
    List<Dictionary> parsed = new ArrayList<>();
    DataFormatter formatter = new DataFormatter();

    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
      if (workbook.getNumberOfSheets() == 0) {
        return parsed;
      }
      Sheet sheet = workbook.getSheetAt(0);
      for (Row row : sheet) {
        Cell keyCell = row.getCell(0);
        Cell valueCell = row.getCell(1);
        if (keyCell == null && valueCell == null) {
          continue;
        }
        String key = keyCell == null ? "" : formatter.formatCellValue(keyCell);
        String value = valueCell == null ? "" : formatter.formatCellValue(valueCell);
        parsed.add(newDictionary(key, value));
      }
    }

    return parsed;
  }

  private List<String> parseCsvLine(String line) {
    List<String> parts = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);
      if (ch == '"') {
        if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
          current.append('"');
          i++;
        } else {
          inQuotes = !inQuotes;
        }
      } else if (ch == ',' && !inQuotes) {
        parts.add(current.toString().trim());
        current.setLength(0);
      } else {
        current.append(ch);
      }
    }
    parts.add(current.toString().trim());

    return parts;
  }

  private byte[] toCsv(List<Dictionary> cards) {
    StringBuilder csv = new StringBuilder();
    csv.append("name,value,name_file,value_file,is_remembered\n");
    for (Dictionary card : cards) {
      csv.append(csvCell(card.getName())).append(',')
        .append(csvCell(card.getValue())).append(',')
        .append(csvCell(card.getNameImg())).append(',')
        .append(csvCell(card.getValueImg())).append(',')
        .append(card.getIsRemembered() != null && card.getIsRemembered() ? "true" : "false")
        .append('\n');
    }
    return csv.toString().getBytes(StandardCharsets.UTF_8);
  }

  private byte[] toXlsx(List<Dictionary> cards) throws IOException {
    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("cards");
      Row header = sheet.createRow(0);
      header.createCell(0).setCellValue("name");
      header.createCell(1).setCellValue("value");
      header.createCell(2).setCellValue("name_file");
      header.createCell(3).setCellValue("value_file");
      header.createCell(4).setCellValue("is_remembered");

      int rowIndex = 1;
      for (Dictionary card : cards) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(safeValue(card.getName()));
        row.createCell(1).setCellValue(safeValue(card.getValue()));
        row.createCell(2).setCellValue(safeValue(card.getNameImg()));
        row.createCell(3).setCellValue(safeValue(card.getValueImg()));
        row.createCell(4).setCellValue(card.getIsRemembered() != null && card.getIsRemembered() ? "true" : "false");
      }

      for (int i = 0; i <= 4; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(out);
      return out.toByteArray();
    }
  }

  private byte[] toDocx(List<Dictionary> cards) throws IOException {
    try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      XWPFParagraph title = document.createParagraph();
      XWPFRun titleRun = title.createRun();
      titleRun.setBold(true);
      titleRun.setText("Dictionary cards export");

      int index = 1;
      for (Dictionary card : cards) {
        XWPFParagraph cardLine = document.createParagraph();
        XWPFRun cardRun = cardLine.createRun();
        cardRun.setBold(true);
        cardRun.setText(index++ + ". " + safeValue(card.getName()) + " -> " + safeValue(card.getValue()));

        if ((card.getNameImg() != null && !card.getNameImg().isBlank()) || (card.getValueImg() != null && !card.getValueImg().isBlank())) {
          XWPFParagraph attachments = document.createParagraph();
          XWPFRun attRun = attachments.createRun();
          attRun.setText("name_file: " + safeValue(card.getNameImg()) + ", value_file: " + safeValue(card.getValueImg()));
        }
      }

      document.write(out);
      return out.toByteArray();
    }
  }

  private String csvCell(String value) {
    String safe = safeValue(value);
    boolean needsQuotes = safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r");
    if (!needsQuotes) {
      return safe;
    }
    return '"' + safe.replace("\"", "\"\"") + '"';
  }

  private String safeValue(String value) {
    return value == null ? "" : value;
  }

  private String safeFileNamePart(String value) {
    String candidate = safeValue(value).trim();
    if (candidate.isEmpty()) {
      return "dictionary";
    }
    return candidate.replaceAll("[^a-zA-Z0-9._-]", "_");
  }

  private Dictionary newDictionary(String name, String value) {
    Dictionary dictionary = new Dictionary();
    dictionary.setName(name == null ? null : name.trim());
    dictionary.setValue(value == null ? "" : value.trim());
    return dictionary;
  }

  private void prepareForFolderSave(Dictionary pair, Folder folder) {
    if (pair.getFolders() == null) {
      pair.setFolders(new ArrayList<>());
    }
    if (!pair.getFolders().contains(folder)) {
      pair.getFolders().add(folder);
    }
    pair.setUser(userService.getCurrentUser());
  }

  private String normalizeKey(String key) {
    if (key == null) {
      return null;
    }
    String normalized = key.trim().toLowerCase(Locale.ROOT);
    return normalized.isEmpty() ? null : normalized;
  }
}
