package at.abcdef.memmaster.service;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.repository.DictionaryRepository;
import at.abcdef.memmaster.service.dictionaryio.DictionaryExportService;
import at.abcdef.memmaster.service.dictionaryio.DictionaryImportService;
import at.abcdef.memmaster.service.dictionaryio.exporter.CsvDictionaryExportStrategy;
import at.abcdef.memmaster.service.dictionaryio.exporter.DocxDictionaryExportStrategy;
import at.abcdef.memmaster.service.dictionaryio.exporter.XlsxDictionaryExportStrategy;
import at.abcdef.memmaster.service.dictionaryio.importer.CsvDictionaryImportStrategy;
import at.abcdef.memmaster.service.dictionaryio.importer.WorkbookDictionaryImportStrategy;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DictionaryServiceBulkImportFromFileTest {

  @Mock
  private DictionaryRepository dictionaryRepository;

  @Mock
  private UserService userService;

  private DictionaryService dictionaryService;
  private AtomicReference<List<Dictionary>> lastSavedBatch;

  @BeforeEach
  void setUp() {
    DictionaryImportService importService = new DictionaryImportService(List.of(
      new CsvDictionaryImportStrategy(),
      new WorkbookDictionaryImportStrategy()
    ));
    DictionaryExportService exportService = new DictionaryExportService(List.of(
      new CsvDictionaryExportStrategy(),
      new XlsxDictionaryExportStrategy(),
      new DocxDictionaryExportStrategy()
    ));

    dictionaryService = new DictionaryService(dictionaryRepository, userService, importService, exportService);
    lastSavedBatch = new AtomicReference<>();
  }

  /** Sets up the stubs needed for every test that actually reaches save. */
  private void stubForSave() {
    User currentUser = new User();
    currentUser.setId(1);
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(dictionaryRepository.getByFolders(anyList())).thenReturn(new ArrayList<>());
    when(dictionaryRepository.saveAll(anyList())).thenAnswer(invocation -> {
      List<Dictionary> batch = invocation.getArgument(0);
      lastSavedBatch.set(batch);
      return batch;
    });
  }

  @Test
  void bulkImportDictionaryFromFileShouldIgnoreCsvDuplicateKeys() {
    stubForSave();
    Folder folder = new Folder();
    folder.setId(10L);

    String csv = "Apple,One\napple,Two\nAPPLE,Three\n";
    MultipartFile file = new MockMultipartFile(
      "file",
      "pairs.csv",
      "text/csv",
      csv.getBytes(StandardCharsets.UTF_8)
    );

    List<Dictionary> saved = dictionaryService.bulkImportDictionaryFromFile(folder, file);

    verify(dictionaryRepository).saveAll(anyList());
    List<Dictionary> savedToRepo = lastSavedBatch.get();
    assertEquals(1, savedToRepo.size());
    assertEquals("Apple", savedToRepo.get(0).getName());
    assertEquals(1, saved.size());
  }

  @Test
  void bulkImportDictionaryFromFileShouldIgnoreXlsxDuplicateKeys() throws IOException {
    stubForSave();
    Folder folder = new Folder();
    folder.setId(11L);

    byte[] xlsx = buildXlsxBytes(new String[][]{
      {"Tree", "A"},
      {"tree", "B"},
      {"TREE", "C"}
    });

    MultipartFile file = new MockMultipartFile(
      "file",
      "pairs.xlsx",
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      xlsx
    );

    List<Dictionary> saved = dictionaryService.bulkImportDictionaryFromFile(folder, file);

    verify(dictionaryRepository).saveAll(anyList());
    List<Dictionary> savedToRepo = lastSavedBatch.get();
    assertEquals(1, savedToRepo.size());
    assertEquals("Tree", savedToRepo.get(0).getName());
    assertEquals(1, saved.size());
  }

  @Test
  void bulkImportDictionaryFromFileShouldRejectUnsupportedExtension() {
    Folder folder = new Folder();
    folder.setId(12L);

    MultipartFile file = new MockMultipartFile(
      "file",
      "pairs.txt",
      "text/plain",
      "k,v".getBytes(StandardCharsets.UTF_8)
    );

    assertThrows(IllegalArgumentException.class,
      () -> dictionaryService.bulkImportDictionaryFromFile(folder, file));

    verify(dictionaryRepository, never()).saveAll(any());
  }

  private byte[] buildXlsxBytes(String[][] rows) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      XSSFSheet sheet = workbook.createSheet("Sheet1");
      for (int i = 0; i < rows.length; i++) {
        sheet.createRow(i).createCell(0).setCellValue(rows[i][0]);
        sheet.getRow(i).createCell(1).setCellValue(rows[i][1]);
      }
      workbook.write(out);
      return out.toByteArray();
    }
  }
}
