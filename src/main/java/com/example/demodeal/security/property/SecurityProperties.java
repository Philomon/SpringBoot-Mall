/**
 * 
 */
package com.example.demodeal.security.property;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "mall.security")
public class SecurityProperties {


    private OAuth2ClientProperties oauth2 = new OAuth2ClientProperties();

    public OAuth2ClientProperties getOauth2() {
        return oauth2;
    }

    public void setOauth2(OAuth2ClientProperties oauth2) {
        this.oauth2 = oauth2;
    }
}

