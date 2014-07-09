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
			.withUser("somas").password("passwordxyz").roles("ADMIN").and()
			.withUser("user2").password("password123").roles("USER");
		
	}
	
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/views/home.html").permitAll()
				.antMatchers("/admin/**", "/views/adminConsole/**").hasRole("ADMIN")
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.and()
			.httpBasic(); 
		
		http.csrf().disable();
	}
}
