package com.learn.java.cache.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
	
	/**
	 * @Async,这个注解要想起作用,必须使用@EnableAsync
	 * SpringBoot会启用一个线程池来调用这个方法
	 */
	@Async//告诉Spring这是一个异步方法
	public void hello() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("如果不添加@Aysn这个注解,这个方法就是同步执行的,必须等3秒之,代码调用我的那个方法才能往下走...");
	}
}
