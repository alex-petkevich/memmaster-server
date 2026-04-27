package at.abcdef.memmaster.service.dictionaryio.importer;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.service.dictionaryio.DictionaryImportStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class WorkbookDictionaryImportStrategy implements DictionaryImportStrategy {

  @Override
  public boolean supports(String lowerFileName) {
    return lowerFileName.endsWith(".xlsx") || lowerFileName.endsWith(".xls");
  }

  @Override
  public List<Dictionary> parse(MultipartFile file) throws IOException {
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

  private Dictionary newDictionary(String name, String value) {
    Dictionary dictionary = new Dictionary();
    dictionary.setName(name == null ? null : name.trim());
    dictionary.setValue(value == null ? "" : value.trim());
    return dictionary;
  }
}

