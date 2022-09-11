package com.learn.java.cache.config;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * https://developer.aliyun.com/learning/course/613/detail/9291?spm=a2c6h.21258778.0.0.57c42d61BS7MhX#directory 《课时7: @Cacheable其他属性》 阿里云 视频
 *
 */
@Configuration
public class MyCacheConfig {
	
	/*
	 * 自定义缓存的key生成规则
	 *
	 * @Bean("myKenGenerator") 注入到SpringBoot容器里面，并且指定名字叫myKenGenerator
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
