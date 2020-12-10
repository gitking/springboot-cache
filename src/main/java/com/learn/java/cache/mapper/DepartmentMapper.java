package com.learn.java.cache.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.learn.java.cache.bean.Department;

@Mapper
public interface DepartmentMapper {
	
	@Select("SELECT DEPAID id, DEPNAME departmentName FROM department WHERE DEPAID = #{id}")
	Department getDeptById(Integer id);
}
