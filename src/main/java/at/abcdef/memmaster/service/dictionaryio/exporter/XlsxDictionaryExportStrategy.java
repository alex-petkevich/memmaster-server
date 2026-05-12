package at.abcdef.memmaster.service.dictionaryio.exporter;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.service.dictionaryio.DictionaryExportStrategy;
import at.abcdef.memmaster.service.dictionaryio.ExportFile;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class XlsxDictionaryExportStrategy implements DictionaryExportStrategy {

  @Override
  public boolean supports(String format) {
    return "xlsx".equals(format) || "excel".equals(format);
  }

  @Override
  public ExportFile export(String baseName, List<Dictionary> cards) throws IOException {
    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("cards");
      Row header = sheet.createRow(0);
      header.createCell(0).setCellValue("name");
      header.createCell(1).setCellValue("value");
      header.createCell(2).setCellValue("name_file");
      header.createCell(3).setCellValue("value_file");
      header.createCell(4).setCellValue("is_remembered");
      header.createCell(5).setCellValue("color");

      int rowIndex = 1;
      for (Dictionary card : cards) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0).setCellValue(safeValue(card.getName()));
        row.createCell(1).setCellValue(safeValue(card.getValue()));
        row.createCell(2).setCellValue(safeValue(card.getNameImg()));
        row.createCell(3).setCellValue(safeValue(card.getValueImg()));
        row.createCell(4).setCellValue(Boolean.toString(card.getIsRemembered() != null && card.getIsRemembered()));
        row.createCell(5).setCellValue(safeValue(card.getColor()));
      }

      for (int i = 0; i <= 5; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(out);
      return new ExportFile(
        baseName + ".xlsx",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        out.toByteArray());
    }
  }

  private String safeValue(String value) {
    return value == null ? "" : value;
  }
}

