package com.learn.java.cache.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.learn.java.cache.bean.Book;

@Service
public class BookService {
	
	/*
	 * 监听指定队列里面的消息,queues也可以是一个数组
	 * @RabbitListener要想生效必须开启@EnableRabbit这个注解
	 */
	@RabbitListener(queues="atguigu.news")
	public void receive(Book book) {
		System.out.println("我已经实时监听到消息了,消息是:" + book);
	}
	
	@RabbitListener(queues="atguigu")
	public void receiveMessageInfo(Message message) {
		System.out.println("这种监听方式可以获取消息的头信息");
		//[B@73c400c8字节数组
		System.out.println("获取消息的内容:" + message.getBody());
		System.out.println("获取消息的头内容:" + message.getMessageProperties());
	}
}
