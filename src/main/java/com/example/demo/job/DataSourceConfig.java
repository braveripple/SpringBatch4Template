package com.example.demo.job;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfig {

	@Bean
	JdbcTemplate jdbcTemplate(DataSource datasource) {
		return new JdbcTemplate(datasource);
	}

}
