package at.abcdef.memmaster.service.dictionaryio.importer;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.service.dictionaryio.DictionaryImportStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvDictionaryImportStrategy implements DictionaryImportStrategy {

  @Override
  public boolean supports(String lowerFileName) {
    return lowerFileName.endsWith(".csv");
  }

  @Override
  public List<Dictionary> parse(MultipartFile file) throws IOException {
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

  private Dictionary newDictionary(String name, String value) {
    Dictionary dictionary = new Dictionary();
    dictionary.setName(name == null ? null : name.trim());
    dictionary.setValue(value == null ? "" : value.trim());
    return dictionary;
  }
}

