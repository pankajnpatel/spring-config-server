package com.pnp.config.jdbcconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.rdbms.EnableRDBMSConfigServer;

@SpringBootApplication
@EnableRDBMSConfigServer
public class JdbcConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(JdbcConfigApplication.class, args);
	}
}
