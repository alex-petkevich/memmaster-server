package at.abcdef.memmaster.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final ApplicationProperties applicationProperties;

    public CacheConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }


    @Bean
    public KeyGenerator keyGenerator() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(this.applicationProperties.getSecurity().getSeedKey().getBytes());

        keyGen.init(256, random);
        return keyGen;
    }
}
