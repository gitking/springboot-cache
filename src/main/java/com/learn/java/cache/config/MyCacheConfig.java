package com.learn.java.cache.config;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyCacheConfig {
	
	/*
	 * 自定义缓存的key生成规则
	 */
	@Bean("myKenGenerator")
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				String keyStr = method.getName() + "[" + Arrays.asList(params).toString() + "]=";
				System.out.println("我自己生成的key" + keyStr);
				return keyStr;
			}
		};
	}
}
