package at.abcdef.memmaster.controllers;

import at.abcdef.memmaster.controllers.dto.WordDTO;
import at.abcdef.memmaster.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/translator")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class TranslatorController {


  public TranslatorController() {
  }

  @PostMapping("/")
  public ResponseEntity<String> lookup(@Valid @RequestBody WordDTO word)
  {

    return ResponseEntity.ok().build();
  }
}
