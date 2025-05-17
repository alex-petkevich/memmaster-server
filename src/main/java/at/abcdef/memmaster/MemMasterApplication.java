package at.abcdef.memmaster;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import at.abcdef.memmaster.config.ApplicationProperties;
import jakarta.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = { "at.abcdef.memmaster" })
@EnableConfigurationProperties({ ApplicationProperties.class })
public class MemMasterApplication {

	private final Environment env;

	private static final Logger log = LoggerFactory.getLogger(MemMasterApplication.class);

	public MemMasterApplication(Environment env) {
		this.env = env;
	}

	@PostConstruct
	public void initApplication() {
		Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
		if (
				activeProfiles.contains("dev") &&
						activeProfiles.contains("prod")
		) {
			log.error(
					"You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time."
			);
		}
		if (
				activeProfiles.contains("dev") &&
						activeProfiles.contains("prod")
		) {
			log.error(
					"You have misconfigured your application! It should not " + "run with both the 'dev' and 'cloud' profiles at the same time."
			);
		}
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MemMasterApplication.class);
		Environment env = app.run(args).getEnvironment();
		logApplicationStartup(env);
	}

	private static void logApplicationStartup(Environment env) {
		String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
		String serverPort = env.getProperty("server.port");
		String contextPath = Optional
				.ofNullable(env.getProperty("server.servlet.context-path"))
				.filter(StringUtils::hasText)
				.orElse("/");
		String hostAddress = "localhost";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.warn("The host name could not be determined, using `localhost` as fallback");
		}
		log.info("""
                         
                         ----------------------------------------------------------
                         \tApplication '{}' is running! Access URLs:
                         \tLocal: \t\t{}://localhost:{}{}
                         \tExternal: \t{}://{}:{}{}
                         \tProfile(s): \t{}
                         ----------------------------------------------------------""",
				env.getProperty("spring.application.name"),
				protocol,
				serverPort,
				contextPath,
				protocol,
				hostAddress,
				serverPort,
				contextPath,
				env.getActiveProfiles()
		);
	}
}
