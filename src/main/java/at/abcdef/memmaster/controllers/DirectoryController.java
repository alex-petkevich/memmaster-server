package at.abcdef.memmaster.controllers;

import at.abcdef.memmaster.controllers.mapper.DirectoryMapper;
import at.abcdef.memmaster.model.Directory;
import at.abcdef.memmaster.service.DirectoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directory")
public class DirectoryController {

   final DirectoryService directoryService;

   final DirectoryMapper directoryMapper;

   public DirectoryController(DirectoryService directoryService, DirectoryMapper directoryMapper) {
      this.directoryService = directoryService;
      this.directoryMapper = directoryMapper;
   }

   @GetMapping("/{type}")
   public ResponseEntity<?> list(@Valid @PathVariable String type)
   {
      List<Directory> directory = directoryService.getDirectoriesByType(type);

      return ResponseEntity.ok(directoryMapper.toEntity(directory));
   }

   @GetMapping("/{type}/{key}")
   public ResponseEntity<?> get(@Valid @PathVariable String type, @PathVariable String key)
   {
      Directory directory = directoryService.getDirectoryByKeyAndType(type, key);

      return ResponseEntity.ok(directoryMapper.toEntity(directory));
   }
}
