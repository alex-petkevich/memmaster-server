package at.abcdef.memmaster.service.dictionaryio;

import at.abcdef.memmaster.model.Dictionary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Service
public class DictionaryExportService {

  private final List<DictionaryExportStrategy> strategies;

  public DictionaryExportService(List<DictionaryExportStrategy> strategies) {
    this.strategies = strategies;
  }

  public ExportFile export(String format, String baseName, List<Dictionary> cards) throws IOException {
    String normalizedFormat = (format == null ? "csv" : format).toLowerCase(Locale.ROOT);
    DictionaryExportStrategy strategy = strategies.stream()
      .filter(s -> s.supports(normalizedFormat))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Unsupported export format: " + format));

    return strategy.export(baseName, cards);
  }
}

