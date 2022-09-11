package com.learn.java.cache;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * https://developer.aliyun.com/learning/course/613 阿里云 尚硅谷 《SpringBoot快速掌握 - 高级应用》
 * 请结合《Spring.docx》word文档查看
 * 一、搭建基本环境
 * 1、导入数据库文件,创建department和employee表
 * 2、创建javabean封装数据
 * 3、整合MyBatis操作数据库
 * 		1、配置数据源信息
 * 		2、使用注解版的MyBatis
 * 			1)、添加@MapperScan扫描指定的包下面的mapper接口
 * 二、快速体验缓存
 *    步骤：
 *    	1、开启基于缓存的注解
 *    	2、标注缓存注解即可@Cacheable,@CacheEvict,@CachePut
 * SpringCache默认使用的是ConcurrentMapCacheManager==ConcurrentMapCache,将数据保存在ConcurrentMap<Object,Object>中
 * 实际开发中经常使用缓存中间件:redis,memcached,ehcache,
 *
 * RabbitMQ自动配置
 * 	spring-boot-autoconfigure-2.3.0.RELEASE.jar里面的
	 * 	org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration这个是自动配置RabbitMQ的类
	 *  org.springframework.boot.autoconfigure.amqp.RabbitProperties
 *  spring-rabbit-2.2.6.RELEASE.jar里面的
	 *  RabbitTemplate:给RabbitMQ发送和接收消息
	 *  RabbitTemplate里面的MessageConverter可以指定序列化规则
	 *  Jackson2JsonMessageConverter使用json序列化,见MyAMQPConfig类中的代码
 *  spring-amqp-2.2.6.RELEASE.jar里面的
 * 		AmqpAdmin:RabbitMQ系统管理功能组件,可以帮忙创建一个队列Queue,可以帮忙创建一个交换器exchange
 * 		AmqpAdmin:创建和删除:Queue队列,Exchange交换器,Binding绑定器
 *   @EnableRabbit + @RabbitListener 可以监听RabbitMQ的消息中间件
 *   Elasticsearch
 *   SpringBoot默认支持俩种技术和ES交互
 *   1、Jest(默认不生效,需要导入Jest的包)
 *   2、SpringData Elasticsearch
 */
@MapperScan("com.learn.java.cache.mapper")
@SpringBootApplication
@EnableCaching//开启基于注解的缓存
@EnableRabbit//启用基于注解的RabbitMQ消息中间件
@EnableAsync//开启异步处理功能
@EnableScheduling//开启基于注解的定时任务
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
