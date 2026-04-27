package at.abcdef.memmaster.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

@Configuration
public class TranslatorHttpClientConfig {

    @Bean
    @Qualifier("translatorRestClient")
    public RestClient translatorRestClient(ApplicationProperties applicationProperties) {
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();

        ApplicationProperties.TranslatorSsl ssl = applicationProperties.getTranslator().getSsl();
        if (StringUtils.hasText(ssl.getTrustStore())) {
            httpClientBuilder.sslContext(buildSslContext(ssl));
        }

        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(httpClientBuilder.build()))
                .build();
    }

    private SSLContext buildSslContext(ApplicationProperties.TranslatorSsl ssl) {
        try {
            KeyStore trustStore = KeyStore.getInstance(ssl.getTrustStoreType());
            try (InputStream in = Files.newInputStream(Path.of(ssl.getTrustStore()))) {
                char[] password = StringUtils.hasText(ssl.getTrustStorePassword()) ? ssl.getTrustStorePassword().toCharArray() : null;
                trustStore.load(in, password);
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            return sslContext;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize translator SSL truststore", ex);
        }
    }
}

