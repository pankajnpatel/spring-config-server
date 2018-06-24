package org.springframework.cloud.config.server.rdbms;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.rdbms.config.RDBMSEnvironmentRepositoryConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Add this annotation to a {@code @Configuration} class to enable Spring Cloud
 * Config Server backed by RDBMS database.
 * 
 * @author Pankaj Patel
 * @see EnableConfigServer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import(RDBMSEnvironmentRepositoryConfiguration.class)
@EnableConfigServer
public @interface EnableRDBMSConfigServer {

}
