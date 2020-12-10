package com.learn.java.cache.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.learn.java.cache.bean.Employee;
import com.learn.java.cache.service.EmployeeService;

@RestController
public class EmployeeController {
	
	@Autowired
	EmployeeService employeeService;
	
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
	
	@GetMapping("/emp/lastname/{lastName}")
	public Employee getEmpByLastName(@PathVariable("lastName")String lastName){
		return employeeService.getEmpByLastName(lastName);
	}
}
