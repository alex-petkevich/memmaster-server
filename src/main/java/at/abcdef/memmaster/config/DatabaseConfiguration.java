package at.abcdef.memmaster.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories("at.abcdef.memmaster.repository")
@EnableTransactionManagement
public class DatabaseConfiguration {
   /*@Bean
   public FlywayMigrationStrategy repairFlyway() {
      return flyway -> {
         // repair each script's checksum
         flyway.repair();
         // before new migrations are executed
         flyway.migrate();
      };
   }*/
}
