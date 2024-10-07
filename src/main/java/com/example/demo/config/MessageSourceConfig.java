package com.example.demo.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;

@Configuration
public class MessageSourceConfig {

	@Bean
	MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
		 return new MessageSourceAccessor(messageSource);
	}

}
