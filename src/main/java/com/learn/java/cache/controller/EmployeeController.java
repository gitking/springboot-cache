package com.learn.java.cache.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.learn.java.cache.bean.Employee;
import com.learn.java.cache.service.EmployeeService;

/**
 * https://developer.aliyun.com/learning/course/613 阿里云 尚硅谷 《SpringBoot快速掌握 - 高级应用》
 *
 */
@RestController
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;

	/**
	 * 从浏览器上面访问:http://localhost:8080/emp/3
	 * SpringBoot会自动将返回的Employee对象转换为json字符串返回给前端
	 * @param id
	 * @return
	 */
	@GetMapping("/emp/{id}")
	public Employee getEmployee(@PathVariable("id")Integer id) {
		System.out.println("controller+查询" + id );
		Employee employee = employeeService.getEmp(id);
		if (employee == null) {
			System.out.println("controller+查询" + id + "结果为null");
		}
		return employee;
	}
	
	/**
	 * http://localhost:8080/emp?lastName=%E6%9D%8E%E5%9B%9B9&id=2&email=123@qqq.com&gender=1&dId=1
	 * @param employee
	 * @return
	 */
	@GetMapping("/emp")
	public Employee update(Employee employee) {
		Employee emp = employeeService.updateEmp(employee);
		return emp;
	}


	/**
	 * 从浏览器上面访问:http://localhost:8080/delEmp?id=1
	 * @param id
	 * @return
	 */
	@GetMapping("/delEmp")
	public String deleteEmp(Integer id) {
		employeeService.deleteEmp(id);
		return "success";
	}
	
	@GetMapping("/delAll")
	public String deleteAll() {
		employeeService.deleteAll();
		return "success";
	}

	/**
	 * @GetMapping("/emp/lastname/{lastName}") 这里不能使用 @GetMapping("/emp/{lastName}")，如果这样的话就跟上面的
	 * @GetMapping("/emp/{id}")重复了，不知道是哪一个了。
	 *
	 *
	 * @param lastName
	 * @return
	 */
	@GetMapping("/emp/lastname/{lastName}")
	public Employee getEmpByLastName(@PathVariable("lastName")String lastName){
		return employeeService.getEmpByLastName(lastName);
	}
}
