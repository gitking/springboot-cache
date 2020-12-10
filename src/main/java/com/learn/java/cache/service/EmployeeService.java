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
 * @author dell
 *
 */
@CacheConfig(cacheNames="emp")//这样每个方法都不需要指定cacheNames={"emp"}了
@Service
public class EmployeeService {
	
	@Autowired
	EmployeeMapper employeeMapper;
	
	/*
	 * @Cacheable将方法的返回结果缓存起来,以后再遇见相同的参数,直接从缓存中获取,这个方法就不会被调用了.
	 * CacheManager管理多个cache组件的,对缓存的真正CRUD操作在Cache组件中,每一个缓存组件有自己唯一一个名字;
	 * @Cacheable的几个属性：
	 * 1、value/cacheNames:指定缓存组件(CacheManager)的名字:,cacheNames的值可以是多个,cacheNames={"emp","temp"}
	 * 2、key：缓存数据使用的key可以中这个属性来指定,如果不指定默认是使用方法参数的值.value就是方法的返回值。
	 *        key也可以编写SpEL表达式,key="#id"表示用参数id的值作为key,key="#root.args[0]" 也是参数id的值
	 *        #a0,#p0的意思都是取出第一个参数的值，#result可以取到方法的返回值
	 *        把key的值设置为getEmp[1],可以这样写
	 * 3、keyGenerator:key的生成器,可以自己指定key的生成器组件id,不过key和keyGenerator二选一使用
	 * 4、cacheManager:指定缓存管理器
	 * 5、cacheResolver：指定缓存解析器,cacheResolver和cacheManager二选一
	 * 6、condition：指定条件,条件符合得情况下才进行缓存,condition="#a0>1" 第一个参数大于1我才缓存
	 *   condition="#a0>1 and #root.methodName eq 'aaa'" 第一个参数大于1并且方法的名字等于aaa时才进行缓存
	 * 7、unless：否定缓存,当unless的指定条件为true，方法的返回值就不会被缓存,
	 * 比如unless="#result== null"的意思是,如果方法的返回结果为null就不缓存了,unless="#a0==2"如果第一个参数的值等于2我就不缓存了
	 * 8、sync：是否使用异步模式,异步模式不支持unless功能
	 * 原理:
	 * 1、
	 */
	@Cacheable(/*cacheNames={"emp"},*/key="#a0",condition="#a0>1",unless="#a0==2")
	public Employee getEmp(Integer id) {
		//Employee要缓存的对象必须实现序列化接口
		System.out.println("查询" + id + "号员工");
		Employee emp = employeeMapper.getEmpById(id);
		return emp;
	}
	
	/*
	 * @Cahceput:即调用方法,又更新缓存
	 * 修改了数据库的某个数据,同时更新缓存.这里要注意,CachePut要跟@Cacheable的key保持一致,否则
	 * @CachePut更新完会安装自己的key的策略往缓存里面放
	 * key="#employee.id"的意思是请求参数的id,
	 * key="#result.id"返回值的ID(@Cacheable不能使用#result.id的值)
	 */
	@CachePut(/*value="emp",*/key="#employee.id")
	public Employee updateEmp(Employee employee) {
		System.out.println("updateEmp:" + employee);
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
	 * allEntries=true清空emp里面的所有缓存
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
