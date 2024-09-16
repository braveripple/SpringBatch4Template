package com.example.demo.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.demo.job.tasklet.tablecopy.TableCopyPrimaryToSecondaryTasklet;
import com.example.demo.job.tasklet.tablecopy.TableCopySecondaryToPrimaryTasklet;
import com.example.demo.job.tasklet.timecard.DisplayTimecardTasklet;
import com.example.demo.job.tasklet.timecard.RegistTimecardTasklet;

@Configuration
public class JobConfig {
	
	@Autowired
	@Qualifier("customJobBuilderFactory")
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	@Qualifier("customStepBuilderFactory")
	private StepBuilderFactory stepBuilderFactory;
	
	// #############################
	// # doSomethingJob
	// #############################
	
    @Bean
    Job doSomethingJob(Step doSomethingStep) throws Exception {
		return jobBuilderFactory.get("doSomethingJob")
				.start(doSomethingStep)
				.build();
	}
	
	@Bean
	Step doSomethingStep() throws Exception {
		
		return stepBuilderFactory.get("doSomethingStep").tasklet(new Tasklet() {

			private final Logger logger = LoggerFactory.getLogger(JobConfig.class);	
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				
				logger.info("Do Something!!");

				final String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
			    
			    if (currentTransactionName != null) {
			        System.out.println("Current Transaction Name: " + currentTransactionName);
			    } else {
			        System.out.println("No active transaction.");
			    }
				
				return RepeatStatus.FINISHED;
			}
		}).allowStartIfComplete(true).build();
	}
	
	// #############################
	// # timecardJob
	// #############################

    @Bean
    Job timecardJob(Step registTimecardStep, Step displayTimecardStep) throws Exception {
		return jobBuilderFactory.get("timecardJob")
				.incrementer(new RunIdIncrementer())
				.start(registTimecardStep)
				.next(displayTimecardStep)
				.validator(timecardJobValidator())
				.build();
	}
    
	@Bean
	JobParametersValidator timecardJobValidator() {
		final DefaultJobParametersValidator validator = 
				new DefaultJobParametersValidator();
		validator.setRequiredKeys(new String[] {"name"});
		return validator;
	}

	@Bean
	Step registTimecardStep(RegistTimecardTasklet registTimecardTasklet) throws Exception {
		return stepBuilderFactory.get("registTimecardStep")
				.tasklet(registTimecardTasklet)
				.build();
	}
	
	@Bean
	Step displayTimecardStep(DisplayTimecardTasklet displayTimecardTasklet) throws Exception {
		return stepBuilderFactory.get("displayTimecardStep")
				.tasklet(displayTimecardTasklet)
				.build();
	}

	// #############################
	// # tableCopyJob
	// #############################
	
    @Bean
    Job tableCopyJob(Step tableCopySecondaryToPrimaryStep, Step tableCopyPrimaryToSecondaryStep) throws Exception {
		return jobBuilderFactory.get("tableCopyJob")
				.incrementer(new RunIdIncrementer())
				.start(tableCopySecondaryToPrimaryStep)
				.next(tableCopyPrimaryToSecondaryStep)
				.build();
	}
	
	@Bean
	Step tableCopySecondaryToPrimaryStep(TableCopySecondaryToPrimaryTasklet tasklet1) throws Exception {
		return stepBuilderFactory.get("tableCopySecondaryToPrimaryStep")
				.tasklet(tasklet1)
				.build();
	}
	
	@Bean
	Step tableCopyPrimaryToSecondaryStep(TableCopyPrimaryToSecondaryTasklet tasklet2,
			@Qualifier("secondaryTransactionManager") PlatformTransactionManager transactionManager) throws Exception {
		return stepBuilderFactory.get("tableCopyPrimaryToSecondaryStep")
				.tasklet(tasklet2)
				.transactionManager(transactionManager)
				.build();
	}


	// #############################
	// # gateJob
	// #############################
	
    @Bean
    Job gateJob(Step gate1Step, Step gate2Step, Step gate3Step) throws Exception {
		return jobBuilderFactory.get("gateJob")
				.start(gate1Step)
				.next(gate2Step)
				.next(gate3Step)
				.build();
	}
	
	@Bean
	Step gate1Step(Tasklet gateTasklet) throws Exception {
		return stepBuilderFactory.get("gate1Step")
				.tasklet(gateTasklet)
				.build();
	}
	
	@Bean
	Step gate2Step(Tasklet gateTasklet) throws Exception {
		return stepBuilderFactory.get("gate2Step")
				.tasklet(gateTasklet)
				.build();
	}

	@Bean
	Step gate3Step(Tasklet gateTasklet) throws Exception {
		return stepBuilderFactory.get("gate3Step")
				.tasklet(gateTasklet)
				.build();
	}

}
