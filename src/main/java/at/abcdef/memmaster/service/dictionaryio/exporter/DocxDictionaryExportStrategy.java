package at.abcdef.memmaster.service.dictionaryio.exporter;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.service.dictionaryio.DictionaryExportStrategy;
import at.abcdef.memmaster.service.dictionaryio.ExportFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class DocxDictionaryExportStrategy implements DictionaryExportStrategy {

  @Override
  public boolean supports(String format) {
    return "docx".equals(format) || "word".equals(format);
  }

  @Override
  public ExportFile export(String baseName, List<Dictionary> cards) throws IOException {
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
      return new ExportFile(
        baseName + ".docx",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        out.toByteArray());
    }
  }

  private String safeValue(String value) {
    return value == null ? "" : value;
  }
}

