package at.abcdef.memmaster.service;

import at.abcdef.memmaster.controllers.dto.WordDTO;
import at.abcdef.memmaster.exception.ApplicationException;
import at.abcdef.memmaster.exception.NotFoundException;
import at.abcdef.memmaster.model.yandex.YDictionary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class TranslatorService {

  private final SettingsService settingsService;

  private final String URL_TEMPLATE = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=%s&lang=%s&text=%s";

  public TranslatorService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  public List<WordDTO> lookup(String text, String sourceLang, String targetLang) {

    String apiKey = settingsService.getSettingValue("api_key");
    if (apiKey.isEmpty()) {
      throw new NotFoundException("Translator API key is not set");
    }

    RestClient restClient = RestClient.create();

    String result = restClient.get()
        .uri(URL_TEMPLATE.formatted(apiKey, sourceLang + "-" + targetLang, text))
        .retrieve()
        .body(String.class);

    final ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    YDictionary dictionary;
    try {
      dictionary = mapper.readValue(result, YDictionary.class);
    } catch (JsonProcessingException e) {
      throw new ApplicationException(e);
    }

    if (dictionary == null || dictionary.getCode() != 200 || dictionary.getDef().isEmpty() || dictionary.getDef().getFirst().getTr().isEmpty()) {
      return null;
    }

    return dictionary.getDef().getFirst().getTr().stream().map(def -> {
      WordDTO word = new WordDTO();
      word.setText(dictionary.getDef().getFirst().getText());
      word.setTranslation(def.getText());
      return word;
    }).toList();
  }
}
