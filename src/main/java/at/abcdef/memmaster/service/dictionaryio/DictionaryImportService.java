package at.abcdef.memmaster.service.dictionaryio;

import at.abcdef.memmaster.model.Dictionary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Service
public class DictionaryImportService {

  private final List<DictionaryImportStrategy> strategies;

  public DictionaryImportService(List<DictionaryImportStrategy> strategies) {
    this.strategies = strategies;
  }

  public List<Dictionary> importFromFile(MultipartFile file) throws IOException {
    String originalFileName = file.getOriginalFilename();
    if (originalFileName == null) {
      throw new IllegalArgumentException("Unsupported file format");
    }

    String lowerName = originalFileName.toLowerCase(Locale.ROOT);
    DictionaryImportStrategy strategy = strategies.stream()
      .filter(s -> s.supports(lowerName))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Unsupported file format"));

    return strategy.parse(file);
  }
}

