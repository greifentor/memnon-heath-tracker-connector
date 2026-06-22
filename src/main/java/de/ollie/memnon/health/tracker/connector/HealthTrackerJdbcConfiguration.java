package de.ollie.memnon.health.tracker.connector;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
class HealthTrackerJdbcConfiguration {

	@Value("${health-tracker.jdbc.datasource.password}")
	private String password;

	@Value("${health-tracker.jdbc.datasource.url}")
	private String url;

	@Value("${health-tracker.jdbc.datasource.username}")
	private String username;
}
