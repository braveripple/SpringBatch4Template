package com.example.demo.job;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DoSomethingJobTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void testJob() throws Exception {
		final JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertThat(jobExecution.getExitStatus(), is(ExitStatus.COMPLETED));
	}

	@Test
	public void testStep() throws Exception {
		final JobExecution jobExecution = jobLauncherTestUtils.launchStep("doSomethingStep");
		assertThat(jobExecution.getExitStatus(), is(ExitStatus.COMPLETED));
	}
	
	@TestConfiguration
	static class JobTestConfig {
		@Bean
		JobLauncherTestUtils myJobLauncherTestUtils() {
			return new JobLauncherTestUtils() {
				@Override
				@Autowired
				public void setJob(@Qualifier("doSomethingJob") Job job) {
					super.setJob(job);
				}
			};
		}
	}

}
