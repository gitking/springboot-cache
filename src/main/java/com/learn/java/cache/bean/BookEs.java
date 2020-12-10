package com.learn.java.cache.bean;

import org.springframework.data.elasticsearch.annotations.Document;

/*
 * @Document可以指定Elasticsearch存储在指定索引和指定类型下面
 */
@Document(indexName="atguigu", type="book")
public class BookEs {
	private Integer id;
	private String bookName;
	private String author;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	@Override
	public String toString() {
		return "BookEs [id=" + id + ", bookName=" + bookName + ", author=" + author + "]";
	}
}
