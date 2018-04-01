/**
 * 
 */
package com.example.demodeal.security;

import com.example.demodeal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 
 * 默认的 UserDetailsService 实现
 * 
 * 不做任何处理，只在控制台打印一句日志，然后抛出异常，提醒业务系统自己配置 UserDetailsService。
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

//	@Autowired
//	PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;


	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#
	 * loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByName(username);
//		throw new UsernameNotFoundException(username);
		return new User(username,user.getPassword(),true, true, true, true,
				 AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_USER"));

	}

}
