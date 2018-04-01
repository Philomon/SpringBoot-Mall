/**
 * 
 */
package com.example.demodeal.security.config;

import com.example.demodeal.security.property.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityCoreConfig {

}


//未完成  令牌配置  资源服务器配置->property