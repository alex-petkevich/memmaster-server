package at.abcdef.memmaster.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("at.abcdef.memmaster.repository")
@EnableTransactionManagement
public class DatabaseConfiguration {
}
