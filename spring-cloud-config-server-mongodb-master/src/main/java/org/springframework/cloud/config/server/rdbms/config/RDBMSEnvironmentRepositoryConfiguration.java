package org.springframework.cloud.config.server.rdbms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.rdbms.environment.RDBMSEnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Pankaj Patel
 */
@Configuration
public class RDBMSEnvironmentRepositoryConfiguration {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Bean
	public EnvironmentRepository environmentRepository() {
		return new RDBMSEnvironmentRepository(jdbcTemplate);
	}

}
