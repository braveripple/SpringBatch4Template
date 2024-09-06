package com.example.demo.job.tasklet.tablecopy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.component.PGCopy;

import lombok.RequiredArgsConstructor;

/**
 * Secondary → Primaryへのテーブルデータコピー
 */
@Component
@RequiredArgsConstructor
public class TableCopySecondaryToPrimaryTasklet implements Tasklet{

	private final Logger logger = LoggerFactory.getLogger(TableCopySecondaryToPrimaryTasklet.class);	

	private final JdbcTemplate primaryJdbcTemplate;
	private final JdbcTemplate secondaryJdbcTemplate;
	
	private final PGCopy primaryPGCopy;
	private final PGCopy secondaryPGCopy;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		logger.info("*** TableCopySecondaryToPrimaryTasklet ***");
		
		logger.info("処理開始前");
		
		logger.info("PrimaryDB - test_customer_secondaryの件数:{}", 
				primaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_customer_secondary;", Long.class)
				);
		logger.info("SecondaryDB - test_customer_secondaryの件数:{}", 
				secondaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_customer_secondary;", Long.class)
				);
		
		logger.info("PrimaryDBのtest_customer_secondaryをデータクリアします");
		
		primaryJdbcTemplate.update("DELETE FROM test_customer_secondary;");

		logger.info("PrimaryDB - test_customer_secondaryの件数:{}", 
				primaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_customer_secondary;", Long.class)
				);

		logger.info("SecondaryDBのtest_customer_secondaryのデータをPrimaryDBのtest_customer_secondaryにコピーします");
		
		final Path file = Paths.get("test_customer_secondary.tsv");
		
		secondaryPGCopy.copyTo(primaryPGCopy, "test_customer_secondary", file);

		logger.info("PrimaryDB - test_customer_secondaryの件数:{}", 
				primaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_customer_secondary;", Long.class)
				);
				
		Files.deleteIfExists(file);
		
		return RepeatStatus.FINISHED;
	
	}

}
