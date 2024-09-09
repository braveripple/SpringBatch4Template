package com.example.demo.job.tasklet.gate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GateService {
		
	public final JdbcTemplate primaryJdbcTemplate;
	
	public boolean isExists(String name, String stepName) {
		return primaryJdbcTemplate.queryForObject(
				"SELECT COUNT(*) > 0 FROM gate WHERE name = ? AND step_name = ?"
				, Boolean.class, name, stepName);
		
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateData(String name, String stepName) {
		primaryJdbcTemplate.update("INSERT INTO gate VALUES(?, ?)", name, stepName);
	}

}
