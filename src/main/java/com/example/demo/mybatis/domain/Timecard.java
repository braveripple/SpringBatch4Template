package com.example.demo.mybatis.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Timecard {
	private final LocalDateTime createdAt;
	private final String name;
}
