package at.abcdef.memmaster.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("at.abcdef.memmaster.repository")
@EnableTransactionManagement
public class DatabaseConfiguration {

   private static final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

   @Bean(initMethod = "migrate")
   public Flyway flyway(DataSource dataSource) {
      log.info("Configuring Flyway migration...");
      return Flyway.configure()
              .dataSource(dataSource)
              .locations("classpath:/db/migration")
              .baselineOnMigrate(true)
              .load();
   }
}
