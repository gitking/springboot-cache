package com.learn.java.cache.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.learn.java.cache.bean.Department;
import com.learn.java.cache.service.DeptServices;

@RestController
public class DeptController {
	
	@Autowired
	DeptServices deptService;
	
	@GetMapping("/dept/{id}")
	public Department getDept(@PathVariable("id")Integer id) {
		System.out.println("有没有走缓存？？？");
		return deptService.getDeptById(id); 
	}
}
