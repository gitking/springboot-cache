package com.learn.java.cache.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.learn.java.cache.bean.BookEs;

/*
 * ElasticsearchRepository<BookEs, Integer>
 * 第一个泛型是类,第二个泛型是类的主键的类型
 */
public interface BookRepository extends ElasticsearchRepository<BookEs, Integer>{
	
	/**
	 * 还可以自定义查询,正常来说这个接口里面不需要写任何方法,就可以直接使用
	 * @param bookName
	 * @return
	 */
	public List<BookEs> findByBookNameLike(String bookName);
}
