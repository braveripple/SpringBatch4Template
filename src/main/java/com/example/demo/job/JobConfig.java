package com.example.demo.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class JobConfig {
	
	private final JobBuilderFactory jobBuilderFactory;
	
	private final StepBuilderFactory stepBuilderFactory;

    @Bean
    Job doSomethingJob() {
		return jobBuilderFactory.get("doSomethingJob")
				.start(doSomethingStep())
				.build();
	}
	
	@Bean
	Step doSomethingStep() {
		
		return stepBuilderFactory.get("doSomethingStep").tasklet(new Tasklet() {

			private final Logger logger = LoggerFactory.getLogger(JobConfig.class);	
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				
				logger.info("Do Something!!");
				
				return RepeatStatus.FINISHED;
			}
		}).build();
	}

}