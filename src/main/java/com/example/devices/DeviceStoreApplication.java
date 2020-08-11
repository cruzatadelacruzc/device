package com.example.devices;

import com.example.devices.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class DeviceStoreApplication implements InitializingBean {

    private final Environment env;

    public DeviceStoreApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DeviceStoreApplication.class);
		addDefaultProfile(application);
		Environment env = application.run(args).getEnvironment();
		logApplicationStartup(env);
    }

	/**
	 * Set a default to use when no profile is configured.
	 *
	 * @param app the Spring application.
	 */
	public static void addDefaultProfile(SpringApplication app) {
		Map<String, Object> defProperties = new HashMap<>();
		/*
		 * The default profile to use when no other profiles are defined
		 * This cannot be set in the application.yml file.
		 * See https://github.com/spring-projects/spring-boot/issues/1219
		 */
		defProperties.put("spring.profiles.default", Constants.PROFILE_DEV);
		app.setDefaultProperties(defProperties);
	}

	private static void logApplicationStartup(Environment env) {
		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}
		String serverPort = env.getProperty("server.port");
		String contextPath = env.getProperty("server.servlet.context-path");
		if (!StringUtils.hasText(contextPath)) {
			contextPath = "/";
		}
		String hostAddress = "localhost";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.warn("The host name could not be determined, using `localhost` as fallback");
		}
		log.info("\n----------------------------------------------------------\n\t" +
						"Application '{}' is running! Access URLs:\n\t" +
						"Local: \t\t{}://localhost:{}{}\n\t" +
						"External: \t{}://{}:{}{}\n\t" +
						"Profile(s): \t{}\n----------------------------------------------------------",
				env.getProperty("spring.application.name"),
				protocol,
				serverPort,
				contextPath,
				protocol,
				hostAddress,
				serverPort,
				contextPath,
				env.getActiveProfiles());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Collection<String> profiles = Arrays.asList(env.getActiveProfiles());
		if (profiles.contains(Constants.PROFILE_DEV) && profiles.contains(Constants.PROFILE_PROD)) {
			log.error("You have misconfigured your application! It should not run " +
					"with both the 'dev' and 'prod' profiles at the same time.");
		}
	}
}
