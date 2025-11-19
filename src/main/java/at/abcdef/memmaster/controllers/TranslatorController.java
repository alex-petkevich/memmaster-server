package at.abcdef.memmaster.controllers;

import at.abcdef.memmaster.controllers.dto.WordDTO;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.service.TranslatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/translator")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class TranslatorController {

  private final TranslatorService translatorService;

  public TranslatorController(TranslatorService translatorService) {
    this.translatorService = translatorService;
  }

  @PostMapping("/")
  public ResponseEntity<List<WordDTO>> lookup(@Valid @RequestBody WordDTO word)
  {
    List<WordDTO> result = translatorService.lookup(word.getText(), word.getLngSource(), word.getLngTarget());

    return ResponseEntity.ok(result);
  }
}
