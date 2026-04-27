package at.abcdef.memmaster.service.dictionaryio;

import at.abcdef.memmaster.model.Dictionary;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DictionaryImportStrategy {
  boolean supports(String lowerFileName);

  List<Dictionary> parse(MultipartFile file) throws IOException;
}

