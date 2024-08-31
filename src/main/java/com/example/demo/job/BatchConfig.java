package com.example.demo.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.job.timecard.DisplayTimecardTasklet;
import com.example.demo.job.timecard.RegistTimecardTasklet;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
	
	private final JobBuilderFactory jobBuilderFactory;
	
	private final StepBuilderFactory stepBuilderFactory;

	// #############################
	// # doSomethingJob
	// #############################
	
    @Bean
    Job doSomethingJob() {
		return jobBuilderFactory.get("doSomethingJob")
				.start(doSomethingStep())
				.build();
	}
	
	@Bean
	Step doSomethingStep() {
		
		return stepBuilderFactory.get("doSomethingStep").tasklet(new Tasklet() {

			private final Logger logger = LoggerFactory.getLogger(BatchConfig.class);	
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				
				logger.info("Do Something!!");
				
				return RepeatStatus.FINISHED;
			}
		}).build();
	}
	
	
	// #############################
	// # timecardJob
	// #############################

	private final RegistTimecardTasklet registTimecardTasklet;
	private final DisplayTimecardTasklet displayTimecardTasklet;
	
    @Bean
    Job timecardJob() {
		return jobBuilderFactory.get("timecardJob")
				.start(registTimecardStep())
				.next(displayTimecardStep())
				.validator(timecardJobValidator())
				.build();
	}
    
	@Bean
	JobParametersValidator timecardJobValidator() {
		final DefaultJobParametersValidator validator = 
				new DefaultJobParametersValidator();
		validator.setRequiredKeys(new String[] {"name"});
		return null;
	}

	@Bean
	Step registTimecardStep() {
		return stepBuilderFactory.get("registTimecardStep")
				.tasklet(registTimecardTasklet)
				.build();
	}
	
	@Bean
	Step displayTimecardStep() {
		return stepBuilderFactory.get("displayTimecardStep")
				.tasklet(displayTimecardTasklet)
				.build();
	}

}