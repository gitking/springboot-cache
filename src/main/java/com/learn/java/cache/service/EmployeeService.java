package com.learn.java.cache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.learn.java.cache.bean.Employee;
import com.learn.java.cache.mapper.EmployeeMapper;

/**
 * 如果你在每个方法上面都写@Cacheable,然后每个@Cacheable里面都指定了相同的cacheNames,那太复杂了
 * 可以使用@CacheConfig抽取公共配置
 *
 * SpringCache默认使用的是ConcurrentMapCacheManager==ConcurrentMapCache,将数据保存在ConcurrentMap<Object,Object>中
 * 实际开发中经常使用缓存中间件:redis,memcached,ehcache, 整合Redis作为缓存
 * @author dell
 */
@CacheConfig(cacheNames="emp")//这样每个方法都不需要指定cacheNames={"emp"}了，抽取缓存的公共配置
//@CacheConfig(cacheNames="emp", cacheManager="employeeCacheManager")//这样每个方法都不需要指定cacheNames={"emp"}了，抽取缓存的公共配置，指定使用employeeCacheManager
@Service
public class EmployeeService {
	
	@Autowired
	EmployeeMapper employeeMapper;
	
	/*
	 * @Cacheable将方法的返回结果缓存起来,以后再遇见相同的参数,直接从缓存中获取,这个方法就不会被调用了.
	 * CacheManager管理多个cache组件的,对缓存的真正CRUD操作在Cache组件中,每一个缓存组件有自己唯一一个名字;
	 * @Cacheable的几个属性：
	 * 1、value/cacheNames(value和cacheNames的作用是一样的):指定缓存组件(CacheManager)的名字:,cacheNames的值可以是多个,cacheNames={"emp","temp"}
	 * 		@Cacheable(cacheNames={"emp"}) 和 @Cacheable(value={"emp"})的意思是一样的，将方法的返回结果放在哪个缓存中，是数组的方式，可以指定多个缓存。
	 * 2、key：缓存数据使用的key可以中这个属性来指定,如果不指定默认是使用方法参数的值.value就是方法的返回值。
	 *        key也可以编写SpEL表达式,key="#id"表示用参数id的值作为key,key="#root.args[0]" 也是参数id的值
	 *        #a0,#p0的意思都是取出第一个参数的值，#result可以取到方法的返回值
	 *        把key的值设置为getEmp[1],可以这样写 key = "#root.methodName + '[' + #id + ']'"
	 * 3、keyGenerator:key的生成器,可以自己指定key的生成器组件id,不过key和keyGenerator二选一使用，使用自己自定义的: keyGenerator="myKenGenerator"
	 * 4、cacheManager:指定缓存管理器, 整合多个缓存管理器的时候可以用到
	 * 5、cacheResolver：指定缓存解析器,cacheResolver和cacheManager二选一
	 * 6、condition：指定条件,条件符合得情况下才进行缓存,condition="#a0>1" 第一个参数大于1我才缓存
	 *   condition="#a0>1 and #root.methodName eq 'aaa'" 第一个参数大于1并且方法的名字等于aaa时才进行缓存
	 * 7、unless：否定缓存,当unless的指定条件为true，方法的返回值就不会被缓存,
	 * 比如unless="#result== null"的意思是,如果方法的返回结果为null就不缓存了,unless="#a0==2"如果第一个参数的值等于2我就不缓存了
	 * 8、sync：是否使用异步模式,异步模式不支持unless功能
	 * 原理:
	 * 1、自动配置类：org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration.class  《spring-boot-autoconfigure-2.3.0.RELEASE.jar》
	 * 2、缓存的配置类:
	 *		org.springframework.boot.autoconfigure.cache.GenericCacheConfiguration
	 * 		org.springframework.boot.autoconfigure.cache.JCacheCacheConfiguration
	 * 		org.springframework.boot.autoconfigure.cache.EhCacheCacheConfiguration
	 * 	    org.springframework.boot.autoconfigure.cache.HazelcastCacheConfiguration
	 * 		org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration
	 * 		org.springframework.boot.autoconfigure.cache.CouchbaseCacheConfiguration
	 * 		org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
	 * 		org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration
	 * 		org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration【SpringBoot默认使用这个】
	 * 		org.springframework.boot.autoconfigure.cache.NoOpCacheConfiguration
	 * 3、上面这么多配置类，哪个配置类会生效呢？SimpleCacheConfiguration 这个默认会生效。
	 * 4、SimpleCacheConfiguration给SpringBoot容器中注册了一个CacheManager:ConcurrentMapCacheManager
	 * 5、可以获取和创建ConcurrentMapCache类型的缓存组件，他的作用将数据保存在ConcurrentMap中；
	 *	运行流程：
	 * 		@Cacheable:
	 * 1、方法运行之前，先去查询Cache(缓存组件)，按照cacheNames指定的名字获取,(CacheManager先获取相应的缓存)，第一次获取缓存如果没有Cache组件会自动创建。
	 * 2、去Cache中查找缓存的内容，使用一个key，默认就是方法的参数。key是按照某种策略生成的，默认是使用keyGenerator生成的，默认使用org.springframework.cache.interceptor.SimpleKeyGenerator生成key.
	 * 	SimpleKeyGenerator生成key的默认策略：1.如果没有参数，key=new SimpleKey(); 2. 如果有一个参数：key的参数的值。 3 如果有多个参数，key=new SimpleKey(params);
	 * 3、没有查到缓存就调用目标方法
	 * 4、将目标方法返回的结果，放进缓存中
	 *
	 * @Cacheable标注的方法执行之前先来检查缓存中有没有这个数据，默认按照参数的值作为key去查询缓存，
	 * 如果没有就运行方法并将结果放入缓存，以后再来调用就可以直接使用缓存中的数据。
	 *
	 * 核心：
	 * 	1)、使用CacheManager【ConcurrentMapCacheManager】按照名字得到Cache【ConcurrentMapCache】组件
	 *  2)、key使用keyGenerator生成的，默认是SimpleKeyGenerator
	 */
	@Cacheable(/*cacheNames={"emp"},*/key="#a0",condition="#a0>1",unless="#a0==2")
	//@Cacheable(cacheNames={"emp"}) 和 @Cacheable(value={"emp"})的意思是一样的
	public Employee getEmp(Integer id) {
		//Employee要缓存的对象必须实现序列化接口
		System.out.println("从数据库查询" + id + "号员工,如果被缓存过之后，这个方法就不会被调用了，我就不会被打印出来");
		Employee emp = employeeMapper.getEmpById(id);
		return emp;
	}
	
	/*
	 * @Cahceput:即调用方法,又更新缓存
	 * 修改了数据库的某个数据,同时更新缓存.这里要注意,CachePut要跟@Cacheable的key保持一致,否则
	 * @CachePut更新完会安装自己的key的策略往缓存里面放
	 * key="#employee.id"的意思是请求参数的id,
	 * key="#result.id"返回值的ID(@Cacheable不能使用#result.id的值)
	 * @CachePut运行时机：
	 * 	1. 先调用目标方法
	 *  2. 将目标方法的结果缓存起来，在方法运行完之后获取方法的返回结果再将结果缓存起来
	 */
	@CachePut(/*value="emp",*/key="#employee.id")
	public Employee updateEmp(Employee employee) {
		System.out.println("updateEmp:这个方法每次都会调用" + employee);
		employeeMapper.updateEmp(employee);
		return employee;
	}
	
	/*
	 * @CacheEvict：缓存清除
	 * @CacheEvict：的属性beforeInvocation=false,默认就是false,代表清空缓存在方法执行之后再清空缓存
	 * ,意思就是如果方法执行失败了,缓存是不会清空的
	 * beforeInvocation=true是在运行方法之前清空缓存,无论你方法执行是否成功都会清空缓存
	 */
	@CacheEvict(/*value="emp",*/ key="#id")
	public void deleteEmp(Integer id) {
		System.out.println("deleteEmp: " + id);
		employeeMapper.deleteEmpById(id);
	}
	
	/*
	 * allEntries=true清空emp里面的所有缓存,allEntries默认为false
	 */
	@CacheEvict(/*value="emp",*/ allEntries=true)
	public void deleteAll() {
		System.out.println("清空emp里面的所有缓存");
	}
	
	/*
	 * @Caching是Cacheable和@CachePut和@CacheEvict的组合,一般用于比较复杂的缓存规则
	 */
	@Caching(//组合注解,定义复杂缓存规则
			cacheable={
					@Cacheable(/*value="emp",*/ key="#lastName")
			},
			put ={//注意标注了CachePut,CachePut意味着这个方法肯定会执行的,等于说上面的Cacheable会失效的,当你
					//拿lastName来查询的时候,即使缓存里面已经有了,由于CachePut的存在这个方法肯定会执行的
					@CachePut(/*value="emp",*/ key="#result.id"),
					@CachePut(/*value="emp",*/ key="#result.email")
			}
	)
	public Employee getEmpByLastName(String lastName) {
		return employeeMapper.getEmpByLastName(lastName);
	}
}
