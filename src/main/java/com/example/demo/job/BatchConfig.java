package com.example.demo.job;

import javax.sql.DataSource;

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
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.job.tasklet.tablecopy.TableCopyPrimaryToSecondaryTasklet;
import com.example.demo.job.tasklet.tablecopy.TableCopySecondaryToPrimaryTasklet;
import com.example.demo.job.tasklet.timecard.DisplayTimecardTasklet;
import com.example.demo.job.tasklet.timecard.RegistTimecardTasklet;

@Configuration
@EnableBatchProcessing
//@RequiredArgsConstructor
public class BatchConfig {
	
	// データソースのDI
	
	@Autowired
	@Qualifier("primaryDataSource")
	private  DataSource primaryDataSource;

	@Autowired
	@Qualifier("jobRepositoryDataSource")
	private  DataSource jobRepositoryDataSource;
	
	@Autowired
	@Qualifier("secondaryDataSource")
	private  DataSource secondaryDataSource;
	
	// トランザクションマネージャーのBean定義
	
	@Bean(name = "primaryTransactionManager")
	@Primary
	PlatformTransactionManager primaryTransactionManager() {
		return new DataSourceTransactionManager(primaryDataSource);
	}
	
	@Bean(name = "jobRepositoryTransactionManager")
	PlatformTransactionManager jobRepositoryTransactionManager() {
		return new DataSourceTransactionManager(jobRepositoryDataSource);
	}
	
	@Bean(name = "secondaryTransactionManager")
	PlatformTransactionManager secondaryTransactionManager() {
		return new DataSourceTransactionManager(secondaryDataSource);
	}
	
	/**
	 * JobRepository
	 * @return
	 * @throws Exception
	 */
	@Bean
	public JobRepository customJobRepository() throws Exception {
		final JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(jobRepositoryDataSource);
		factory.setTransactionManager(jobRepositoryTransactionManager());
		return factory.getObject();
	}

	/**
	 * JobBuilderFactory
	 * @return
	 * @throws Exception
	 */
	@Bean(name = "customJobBuilderFactory")
	public JobBuilderFactory customJobBuilderFactory() throws Exception {
		return new JobBuilderFactory(customJobRepository());
	}
	
	/**
	 * StepBuilderFactory
	 * @return
	 * @throws Exception
	 */
	@Bean(name = "customStepBuilderFactory")
	public StepBuilderFactory customStepBuilderFactory() throws Exception {
		return new StepBuilderFactory(customJobRepository(), primaryTransactionManager());
	}
	// #############################
	// # doSomethingJob
	// #############################
	
    @Bean
    Job doSomethingJob() throws Exception {
		return customJobBuilderFactory().get("doSomethingJob")
				.incrementer(new RunIdIncrementer())
				.start(doSomethingStep())
				.build();
	}
	
	@Bean
	Step doSomethingStep() throws Exception {
		
		return customStepBuilderFactory().get("doSomethingStep").tasklet(new Tasklet() {

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

	@Autowired
	private RegistTimecardTasklet registTimecardTasklet;

	@Autowired
	private DisplayTimecardTasklet displayTimecardTasklet;
	
    @Bean
    Job timecardJob() throws Exception {
		return customJobBuilderFactory().get("timecardJob")
				.incrementer(new RunIdIncrementer())
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
		return validator;
	}

	@Bean
	Step registTimecardStep() throws Exception {
		return customStepBuilderFactory().get("registTimecardStep")
				.tasklet(registTimecardTasklet)
				.build();
	}
	
	@Bean
	Step displayTimecardStep() throws Exception {
		return customStepBuilderFactory().get("displayTimecardStep")
				.tasklet(displayTimecardTasklet)
				.build();
	}

	// #############################
	// # tableCopyJob
	// #############################
	
    @Bean
    Job tableCopyJob() throws Exception {
		return customJobBuilderFactory().get("tableCopyJob")
				.incrementer(new RunIdIncrementer())
				.start(tableCopySecondaryToPrimaryStep())
				.next(tableCopyPrimaryToSecondaryStep())
				.build();
	}
    
	@Autowired
	private TableCopySecondaryToPrimaryTasklet tasklet1;

	@Autowired
	private TableCopyPrimaryToSecondaryTasklet tasklet2;
	
	@Bean
	Step tableCopySecondaryToPrimaryStep() throws Exception {
		return customStepBuilderFactory().get("tableCopySecondaryToPrimaryStep")
				.tasklet(tasklet1)
				.build();
	}
	
	@Bean
	Step tableCopyPrimaryToSecondaryStep() throws Exception {
		return customStepBuilderFactory().get("tableCopyPrimaryToSecondaryStep")
				.tasklet(tasklet2)
				.transactionManager(secondaryTransactionManager())
				.build();
	}
}