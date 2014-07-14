package com.att.developer.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import net.sf.ehcache.CacheManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Configuration
@EnableWebMvcSecurity
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

	@Inject
	private DataSource dataSource;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth
			.inMemoryAuthentication()
			.withUser("somas").password("password123").roles("ADMINISTRATOR").and()
			.withUser("user2").password("password123").roles("USER");
		
	}
	
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/views/home.html").permitAll()
				.antMatchers("/admin/**", "/views/adminConsole/**").hasRole("ADMINISTRATOR")
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.and()
			.httpBasic(); 
		
		http.csrf().disable();
	}
	
	
	@Bean
	public EhCacheBasedAclCache aclCache(){
		EhCacheBasedAclCache aclCache = new EhCacheBasedAclCache(ehCacheFactoryBean().getObject(), permissionGrantingStrategy(), aclAuthorizationStrategy());
		return aclCache;
	}
	
	
	@Bean
	public BasicLookupStrategy basicLookupStrategy(){
		return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), permissionGrantingStrategy());
	}
	
	
	@Bean 
	
	public JdbcMutableAclService aclService(){
		return new JdbcMutableAclService(dataSource, basicLookupStrategy(), aclCache());
	}
	

	
	@Bean 
	protected EhCacheFactoryBean ehCacheFactoryBean(){

		EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
		CacheManager cacheManager = CacheManager.create();
		ehCacheFactoryBean.setCacheManager(cacheManager);
		ehCacheFactoryBean.setCacheName("aclCache");
		
		return ehCacheFactoryBean;

	}
	
	//Can't see reason to make this public Bean
	private PermissionGrantingStrategy permissionGrantingStrategy(){
		return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
	}
	
	
	//Can't see reason to make this public Bean	
	private AclAuthorizationStrategy aclAuthorizationStrategy(){
		return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ADMINISTRATOR"));
	}
	
	
}
