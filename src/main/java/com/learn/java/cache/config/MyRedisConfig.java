package com.learn.java.cache.config;

import java.rmi.UnknownHostException;
import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * https://developer.aliyun.com/learning/course/613/detail/9296?spm=a2c6h.21258778.0.0.57c42d61BS7MhX#directory 《课时12: RedisTemplate&amp;序列化机》 阿里云视频 尚硅谷
 *
 */
@Configuration
public class MyRedisConfig {

	/**
	 * 使用JSON进行序列化
	 * @param redisConnectionFactory
	 * @return
	 * @throws UnknownHostException
	 */
	@Bean
	public RedisTemplate<Object, Object> empRedisTemplate(RedisConnectionFactory redisConnectionFactory)
	throws UnknownHostException{
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setConnectionFactory(redisConnectionFactory);
		//修改RedisTemplate的默认序列化机制
		Jackson2JsonRedisSerializer<Object> ser = new Jackson2JsonRedisSerializer<Object>(Object.class);
		template.setDefaultSerializer(ser);
		System.out.println("设置进去没???" + template.getDefaultSerializer());
		return template;
	}

	/**
	 * 自定义RedisCacheManager
     * SpringBoot 2.3 整合redis缓存自定义序列化
	 * https://blog.csdn.net/weixin_44757206/article/details/106407916
	 * 这个是SpringBoot 2.3 之前的redis修改序列化的方法
	 * https://blog.csdn.net/hehuihh/article/details/88526902
	 */
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				// 设置缓存的超时时间
                .entryTtl(Duration.ofDays(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager.builder(factory).cacheDefaults(cacheConfiguration).build();
	}
}
