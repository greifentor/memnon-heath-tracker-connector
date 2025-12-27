package de.ollie.memnon.health.tracker.connector;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
class JdbcConfiguration {

	@Value("${jdbc.datasource.password}")
	private String password;

	@Value("${jdbc.datasource.url}")
	private String url;

	@Value("${jdbc.datasource.username}")
	private String username;
}
