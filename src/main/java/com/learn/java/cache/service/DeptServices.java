package com.learn.java.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.learn.java.cache.bean.Department;
import com.learn.java.cache.mapper.DepartmentMapper;

@Service
public class DeptServices {

	@Autowired
	DepartmentMapper departmentMapper;
	
	@Cacheable(cacheNames="dept")
	public Department getDeptById(Integer id) {
		System.out.println("查询部门" + id + ",现在没有走缓存");
		Department dept = departmentMapper.getDeptById(id);
		return dept;
	}
}
