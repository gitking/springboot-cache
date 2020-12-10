package com.learn.java.cache.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {
	
	/*
	 * A cron-like expression, extending the usual UN*X definition to include triggers
	 * on the second, minute, hour, day of month, month, and day of week.
	 * <p>For example, {@code "0 * * * * MON-FRI"} means once per minute on weekdays
	 * (at the top of the minute - the 0th second).
	 * <p>The fields read from left to right are interpreted as follows.
	 * 
	 * 实例:0 * * * * MON-FRI 周一到周五每分钟启动一次 0 和7都能代表周日
	 * 需要开启@EnableScheduling开启基于注解的定时任务
	 * https://cron.qqe2.com/
	 * 
	 * [0 0/5 14,18 * * ?] 每天14点整和18点整,每隔5分钟执行一次
	 * [0 15 10 ? * 1-6] 每个月的周一至周六10:15分执行一次
	 * [0 0 2 ? * 6L]每个月的最后一个周六凌晨2点执行一次
	 * [0 0 2 LW * ?]每个月的最后一个工作日凌晨2点执行一次
	 * [0 0 2-4 ? * 1#1]每个月的第一个周一凌晨2点到4点期间,每个整点都执行一次;
	 */
	//@Scheduled(cron="0 * * * * *")//SpringBoot应用启动之后这个定时任务就会被触发
	//@Scheduled(cron="0,1,2,3 * * * * *")//SpringBoot应用启动之后这个定时任务就会被触发
	//@Scheduled(cron="0-3 * * * * *")//SpringBoot应用启动之后这个定时任务就会被触发,0到3秒,每一秒都会启动一次
	@Scheduled(cron="0/3 * * * * *")//SpringBoot应用启动之后这个定时任务就会被触发,每3秒执行一次
	public void hello() {
		System.out.println("SpringBoot的定时任务....");
	}
}
