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
 * Primary→Secondaryへのテーブルデータコピー
 */
@Component
@RequiredArgsConstructor
public class TableCopyPrimaryToSecondaryTasklet implements Tasklet{

	private final Logger logger = LoggerFactory.getLogger(TableCopyPrimaryToSecondaryTasklet.class);	

	private final JdbcTemplate primaryJdbcTemplate;
	private final JdbcTemplate secondaryJdbcTemplate;
	
	private final PGCopy primaryPGCopy;
	private final PGCopy secondaryPGCopy;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		logger.info("*** TableCopyPrimaryToSecondaryTasklet ***");
		
		logger.info("処理開始前");
		
		logger.info("PrimaryDB - test_customer_primaryの件数:{}", 
				primaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_customer_primary;", Long.class)
				);
		logger.info("SecondaryDB - test_customer_primaryの件数:{}", 
				secondaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_customer_primary;", Long.class)
				);
		
		logger.info("SecondaryDBのtest_customer_primaryをデータクリアします");
		
		secondaryJdbcTemplate.update("DELETE FROM test_customer_primary;");

		logger.info("SecondaryDB - test_customer_primaryの件数:{}", 
				secondaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_customer_primary;", Long.class)
				);

		logger.info("PrimaryDBのtest_customer_primaryのデータをSecondaryDBのtest_customer_primaryにコピーします");
		
		final Path file = Paths.get("test_customer_primary.tsv");
		
		primaryPGCopy.copyTo(secondaryPGCopy, "test_customer_primary", file);
		
		logger.info("SecondaryDB - test_customer_primaryの件数:{}", 
				secondaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_customer_primary;", Long.class)
				);
				
		Files.deleteIfExists(file);
		
		return RepeatStatus.FINISHED;
	
	}

}
