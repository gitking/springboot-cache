package com.learn.java.cache.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.java.cache.service.AsyncService;

@RestController
public class AsyncController {
	
	@Autowired
	AsyncService asyncService;
	
	@GetMapping("/helloAsync")
	public String hello() {
		asyncService.hello();//会异步运行
		return "success";
	}
}
