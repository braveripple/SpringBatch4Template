package com.example.demo.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

	// JobRepositoryのBean定義
	
	/**
	 * JobRepository
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Primary
	JobRepository customJobRepository(@Qualifier("jobRepositoryDataSource") javax.sql.DataSource dataSource,
            @Qualifier("jobRepositoryTransactionManager") PlatformTransactionManager transactionManager) throws Exception {
		final JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTransactionManager(transactionManager);
		factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");
        factory.setValidateTransactionState(true);
        factory.setDatabaseType("POSTGRES");
		return factory.getObject();
	}

	/** 
	 * 
	 * @param jobRepository
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Primary
    JobLauncher customJobLauncher(@Qualifier("customJobRepository") JobRepository jobRepository) throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
	
	/**
	 * JobBuilderFactory
	 * @return
	 * @throws Exception
	 */
	@Bean(name = "customJobBuilderFactory")
	JobBuilderFactory customJobBuilderFactory(@Qualifier("customJobRepository") JobRepository jobRepository) throws Exception {
		return new JobBuilderFactory(jobRepository);
	}
	
	/**
	 * StepBuilderFactory
	 * @return
	 * @throws Exception
	 */
	@Bean(name = "customStepBuilderFactory")
	StepBuilderFactory customStepBuilderFactory(@Qualifier("customJobRepository") JobRepository jobRepository, @Qualifier("primaryTransactionManager") PlatformTransactionManager transactionManager) throws Exception {
		return new StepBuilderFactory(jobRepository, transactionManager);
	}

}