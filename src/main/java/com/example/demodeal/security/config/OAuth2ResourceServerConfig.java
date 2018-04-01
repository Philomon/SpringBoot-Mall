/**
 * 
 */
package com.example.demodeal.security.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * 资源服务器配置
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {


		http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"用户注册页面").permitAll()
				.anyRequest().authenticated();

		//如何登录？ springsecurity有默认的登录url，可以根据demo的html查询

	}
	
}