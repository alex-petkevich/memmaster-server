package at.abcdef.memmaster.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TranslatorService {

  private final SettingsService settingsService;

  private String URL_TEMPLATE = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=%s&lang=%s&text=%s";

  public TranslatorService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  public String lookup(String text, String sourceLang, String targetLang) {

    String apiKey = settingsService.getSettingValue("api_key");
    if (apiKey.isEmpty()) {
      throw new RuntimeException("Translator API key is not set");
    }

    RestClient restClient = RestClient.create();

    String result = restClient.get()
        .uri(URL_TEMPLATE.formatted(apiKey, sourceLang + "-" + targetLang, text))
        .retrieve()
        .body(String.class);



    return result;
  }
}
