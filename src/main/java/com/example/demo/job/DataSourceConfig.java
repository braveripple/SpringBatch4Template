package com.example.demo.job;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.component.PGCopy;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Bean(name = "primaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.primary")
	@Primary
	DataSource primaryDataSource() {
		final HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder.create().build();
		dataSource.setPoolName("Primary-DataSource HikariPool");
		return dataSource;
	}
	
	@Bean(name = "jobRepositoryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.jobrepository")
	@BatchDataSource
	DataSource jobRepositoryDataSource() {
		final HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder.create().build();
		dataSource.setPoolName("JobRepository-DataSource HikariPool");
		return dataSource;
	}
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.secondary")
	DataSource secondaryDataSource() {
		final HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder.create().build();
		dataSource.setPoolName("Secondary-DataSource HikariPool");
		return dataSource;
	}
	
	@Bean
	JdbcTemplate primaryJdbcTemplate() {
		return new JdbcTemplate(primaryDataSource());
	}
	
	@Bean
	JdbcTemplate secondaryJdbcTemplate() {
		return new JdbcTemplate(secondaryDataSource());
	}
	
	@Bean
	PGCopy primaryPGCopy() {
		return new PGCopy(primaryDataSource());
	}
	
	@Bean
	PGCopy secondaryPGCopy() {
		return new PGCopy(secondaryDataSource());
	}
	

}
