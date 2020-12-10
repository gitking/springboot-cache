package com.learn.java.cache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter{

	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//定制请求的授权规则
		http.authorizeRequests()
		.antMatchers("/").permitAll()//根目录/permitAll所有人都可以访问
		.antMatchers("/level1/**").hasRole("VIP1")//level1下面的所有资源必须拥有VIP1这个角色才能访问
		.antMatchers("/level2/**").hasRole("VIP2")
		.antMatchers("/level3/**").hasRole("VIP3");
		
		/*
		 * 开启自动配置的登录功能,可以看一下formLogin的源码上面的注释
		 * 如果没有登录或者没有权限,就会来到登陆页面
		 * 1、会跳转到/login登录页面,这个页面是SpringBoot自动生成的,注意默认post形式的/login请求代表处理登录,如果是get形式的请求/login代表打开登录页面,一旦自定义之后,那你自定义的
		 * 就会代替/login
		 * 2、重定向到/login?error表示登录失败
		 * 3、更多详细规定,参考源码上面的注释
		 */
		//http.formLogin();默认使用这个,如果需要自定义登录页面使用下面这个
		http.formLogin()
		.usernameParameter("user")//处理登录的时候，post请求默认的参数为username,不过key使用usernameParameter自定义参数名称
		.passwordParameter("pwd")
		.loginPage("/userlogin");//自定义登录页面
		//.loginProcessingUrl("/");//自定义登录页面
		
		/**
		 * 开启自动配置的注销功能
		 * 1、访问/logout(这个logout请求必须是post请求)表示用户注销,清空session
		 * 2、注销成功会跳转到/logout?logout页面,不过这个可以使用logoutSuccessUrl更改注销成功后的跳转地址
		 */
		http.logout().logoutSuccessUrl("/")//注销成功后跳转到首页
		;
		
		//开启记住我功能,登录成功之后,将Cookie发送给浏览器进行保存,以后登录浏览器会自动带上这个Cookie,只要通过检查就可以免登录
		//点击注销会立即删除Cookie
		http.rememberMe().rememberMeParameter("remeber");//自定义参数名字
	}
	
	/*
	 * 登陆如果报这个错误,可以这样解决.password("{noop}123456")
	 * https://www.cnblogs.com/jpfss/p/11004828.html
	 * https://www.javacodemonk.com/spring-security-5-there-is-no-passwordencoder-mapped-for-the-id-b0503f3d
	 * https://blog.csdn.net/qq_21336651/article/details/85261969
	 * There was an unexpected error (type=Internal Server Error, status=500).
	 * There is no PasswordEncoder mapped for the id "null"
	 * java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
	 *	at org.springframework.security.crypto.password.DelegatingPasswordEncoder$UnmappedIdPasswordEncoder.matches(DelegatingPasswordEncoder.java:250)
	 *	at org.springframework.security.crypto.password.DelegatingPasswordEncoder.matches(DelegatingPasswordEncoder.java:198)
	 *	at 
	 */
	//定义认证规则
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//inMemoryAuthentication保存在内存中
		auth.inMemoryAuthentication().withUser("zhangsan").password("{noop}123456")
		.roles("VIP1","VIP2")
		.and()
		.withUser("lisi").password("{noop}123456").roles("VIP3","VIP2")
		.and()
		.withUser("wangwu").password("{noop}123456").roles("VIP1","VIP3");
	}
	
	
	/*
	 * 这样也可以解决(non-Javadoc)There is no PasswordEncoder mapped for the id "null"
	 * 这个报错
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#userDetailsService()
	 */
//	@Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails userDetails = User.withDefaultPasswordEncoder()
//				.username("zhangsan")
//				.password("123456")这个似乎只能添加一个用户
//				.roles("VIP1","VIP2")
//				.build();
//		return new InMemoryUserDetailsManager(userDetails);
//	}
	

//	 https://www.javacodemonk.com/spring-security-5-there-is-no-passwordencoder-mapped-for-the-id-b0503f3d
//	 @Bean
//	    public UserDetailsService userDetailsService() {
//	        User.UserBuilder users = User.withDefaultPasswordEncoder();
//	        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//	        manager.createUser(users.username("user").password("password1").roles("USER").build());
//	        manager.createUser(users.username("admin").password("password2").roles("ADMIN").build());
//	        return manager;
//
//	    }
}
