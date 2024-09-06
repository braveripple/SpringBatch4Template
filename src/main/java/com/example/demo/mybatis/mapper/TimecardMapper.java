package com.example.demo.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.mybatis.domain.Timecard;

@Mapper
public interface TimecardMapper {
	
	public List<Timecard> selectList();
	
	public long countAll();

}
