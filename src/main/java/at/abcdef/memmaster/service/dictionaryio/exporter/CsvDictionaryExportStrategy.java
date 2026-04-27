package at.abcdef.memmaster.service.dictionaryio.exporter;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.service.dictionaryio.DictionaryExportStrategy;
import at.abcdef.memmaster.service.dictionaryio.ExportFile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvDictionaryExportStrategy implements DictionaryExportStrategy {

  @Override
  public boolean supports(String format) {
    return "csv".equals(format);
  }

  @Override
  public ExportFile export(String baseName, List<Dictionary> cards) {
    StringBuilder csv = new StringBuilder();
    csv.append("name,value,name_file,value_file,is_remembered\n");
    for (Dictionary card : cards) {
      csv.append(csvCell(card.getName())).append(',')
        .append(csvCell(card.getValue())).append(',')
        .append(csvCell(card.getNameImg())).append(',')
        .append(csvCell(card.getValueImg())).append(',')
        .append(card.getIsRemembered() != null && card.getIsRemembered())
        .append('\n');
    }

    return new ExportFile(baseName + ".csv", "text/csv; charset=UTF-8", csv.toString().getBytes(StandardCharsets.UTF_8));
  }

  private String csvCell(String value) {
    String safe = value == null ? "" : value;
    boolean needsQuotes = safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r");
    if (!needsQuotes) {
      return safe;
    }
    return '"' + safe.replace("\"", "\"\"") + '"';
  }
}

