package com.att.developer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth
			.inMemoryAuthentication()
			.withUser("somas").password("password123").roles("USER").and()
			.withUser("user2").password("password123").roles("USER");
		
	}

	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("**")
/*				.hasRole("USER").anyRequest().authenticated();*/
				.permitAll()
				.anyRequest().authenticated();
/**				.and()
			.formLogin()
				.and()
			.httpBasic(); */
		
		http.csrf().disable();
	}
}
