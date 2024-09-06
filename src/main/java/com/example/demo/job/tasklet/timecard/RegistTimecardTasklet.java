package com.example.demo.job.tasklet.timecard;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;

@Component
@StepScope
@RequiredArgsConstructor
public class RegistTimecardTasklet implements Tasklet{

	private final Logger logger = LoggerFactory.getLogger(RegistTimecardTasklet.class);	

	private final JdbcTemplate primaryJdbcTemplate;

	@Value("#{jobParameters['name']}")
	private String paramName;

	@Override
	@Transactional
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info("*** RegistTimecardTasklet ***");

		Arrays.stream(paramName.split(",")).map(String::trim).forEach(name->{
			logger.info("{}さんを登録します。", name);
			primaryJdbcTemplate.update("INSERT INTO timecard (name) VALUES(?);", name);
			logger.info("{}さんを登録しました。", name);
		});

		String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
	    
	    if (currentTransactionName != null) {
	        System.out.println("Current Transaction Name: " + currentTransactionName);
	    } else {
	        System.out.println("No active transaction.");
	    }	   
		return RepeatStatus.FINISHED;
	
	}

}
