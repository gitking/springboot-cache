package com.learn.java.cache.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.learn.java.cache.bean.Employee;

/**
 * 用org.apache.ibatis.annotations.Mapper注解标识这是一个MyBatis的Mapper
 * https://developer.aliyun.com/learning/course/613 阿里云 尚硅谷 《SpringBoot快速掌握 - 高级应用》
 */
@Mapper
public interface EmployeeMapper {
	
	@Select("select * from employee where id = #{id}")
	public Employee getEmpById(Integer id);
	
	@Update("update employee set LAST_NAME=#{lastName}, email=#{email}, gender=#{gender},d_id=#{dId} where id=#{id}")
	public void updateEmp(Employee employee);
	
	@Delete("DELETE FROM employee WHERE id=#{id}")
	public void deleteEmpById(Integer id);
	
	@Insert("INSERT INTO employee(lastName, email, gender, d_id) VALUES(#{lastName},#{email},#{gender},#{dId})")
	public void insertEmp(Employee employee);
	
	@Select("select * from employee where LAST_NAME = #{lastName}")
	public Employee getEmpByLastName(String lastName);
}
