package com.example.demo.job;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TimecardJobTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JdbcTemplate primaryJdbcTemplate;
	
	@Test
	public void testJob() throws Exception {
		
		final JobParameters param = new JobParametersBuilder().addString("name", "tanaka").toJobParameters();
		
		final JobExecution jobExecution = jobLauncherTestUtils.launchJob(param);

		assertThat(jobExecution.getExitStatus(), is(ExitStatus.COMPLETED));
		
		Integer x = primaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM timecard;", Integer.class);
		assertThat(x, greaterThan(0));
		
	}
	
	@TestConfiguration
	static class JobTestConfig {
		@Bean
		JobLauncherTestUtils jobLauncherTestUtils() {
			return new JobLauncherTestUtils() {
				@Override
				@Autowired
				public void setJob(@Qualifier("timecardJob") Job job) {
					super.setJob(job);
				}
			};
		}
	}

}
