package com.example.demo.job.tasklet.gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Gate
 */
@Component
@RequiredArgsConstructor
public class GateTasklet implements Tasklet {

	private final Logger logger = LoggerFactory.getLogger(GateTasklet.class);

	private final GateService gateService;
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		final String name = (String) chunkContext.getStepContext().getJobParameters().get("name");
		final String stepName = chunkContext.getStepContext().getStepName();
		
		logger.info("門：{}", stepName);
		logger.info("||　　　|", stepName);
		logger.info("||o　　 |", stepName);
		logger.info("||　　　|", stepName);
		
		if (gateService.isExists(name, stepName)) {
			logger.info("{}は{}の突破に成功した。", name, stepName);
		} else {
			logger.info("{}は{}の突破に失敗した。", name, stepName);
			gateService.updateData(name, stepName);
			logger.info("{}は{}の鍵をもらった。", name, stepName);
			throw new IllegalStateException("失敗");
		}

		return RepeatStatus.FINISHED;

	}
}
