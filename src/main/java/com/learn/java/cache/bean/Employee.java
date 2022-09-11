package com.learn.java.cache.bean;

import java.io.Serializable;

public class Employee implements Serializable{
	private Integer id;
	
	/*
	 * 驼峰命名法
	 * 如果开启了mybatis的属性mybatis.configuration.map-underscore-to-camel-case=true
	 * 那么表的字段可以是last_name,myBatis会自动表中的字段last_name把下划线后面的第一个字符变为大写再来映射
	 */
	private String lastName;
	private String email;
	private Integer gender;
	private Integer dId;
	
	public Employee() {
		super();
	}
	public Employee(Integer id, String lastName, String email, Integer gender, Integer dId) {
		super();
		this.id = id;
		this.lastName = lastName;
		this.email = email;
		this.gender = gender;
		this.dId = dId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getdId() {
		return dId;
	}
	public void setdId(Integer dId) {
		this.dId = dId;
	}
	@Override
	public String toString() {
		return "Employee [id=" + id + ", lastName=" + lastName + ", email=" + email + ", gender=" + gender + ", dId="
				+ dId + "]";
	}
}
