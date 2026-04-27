package at.abcdef.memmaster.service.dictionaryio;

import at.abcdef.memmaster.model.Dictionary;

import java.io.IOException;
import java.util.List;

public interface DictionaryExportStrategy {
  boolean supports(String format);

  ExportFile export(String baseName, List<Dictionary> cards) throws IOException;
}

