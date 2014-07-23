package com.att.developer.config;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;

import net.sf.ehcache.CacheManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.PermissionCacheOptimizer;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.att.developer.security.AttPasswordEncoder;
import com.att.developer.security.CustomAclLookupStrategy;
import com.att.developer.security.CustomPermissionGrantingStrategy;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableWebMvcSecurity
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

	@Inject
	private DataSource dataSource;
	
	@Resource(name="attUserDetailsService")
	private UserDetailsService userDetailsService;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth
			.userDetailsService(userDetailsService)
			.passwordEncoder(new AttPasswordEncoder());
		
	}
	
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/views/home.html").permitAll()
				.antMatchers("/resources/**").permitAll()
				.antMatchers("/admin/**", "/views/adminConsole/**", "/apiBundle/add/**").hasRole("SYS_ADMIN")
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/auth/login")
				.defaultSuccessUrl("/auth/loginsuccess")
				.permitAll();
		
		http.csrf().disable();
		
		
	}

	
	
	
	@Bean
	public EhCacheBasedAclCache aclCache(){
		EhCacheBasedAclCache aclCache = new EhCacheBasedAclCache(ehCacheFactoryBean().getObject(), permissionGrantingStrategy(), aclAuthorizationStrategy());
		return aclCache;
	}
	
	
	@Bean
	public CustomAclLookupStrategy basicLookupStrategy(){
		return new CustomAclLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), permissionGrantingStrategy());
	}
	
	
	@Bean 
	public JdbcMutableAclService aclService(){
		JdbcMutableAclService aclService = new JdbcMutableAclService(dataSource, basicLookupStrategy(), aclCache());
		
		aclService.setClassIdentityQuery("SELECT LAST_INSERT_ID()");
		aclService.setSidIdentityQuery("SELECT LAST_INSERT_ID()");
		
		
		return aclService;
	}

	
	@Bean 
	protected EhCacheFactoryBean ehCacheFactoryBean(){

		EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
		CacheManager cacheManager = CacheManager.create();
		ehCacheFactoryBean.setCacheManager(cacheManager);
		ehCacheFactoryBean.setCacheName("aclCache");
		
		return ehCacheFactoryBean;

	}
	
	
	@Bean
	protected PermissionGrantingStrategy permissionGrantingStrategy(){
		return new CustomPermissionGrantingStrategy(new ConsoleAuditLogger());
	}
	
	
	@Bean
	protected AclAuthorizationStrategy aclAuthorizationStrategy(){
		return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ADMINISTRATOR"));
	}
	

	
	@Bean
	public JdbcTemplate jdbcTemplate(){
		return new JdbcTemplate(this.dataSource);
	}

	
	@Bean
	protected PermissionEvaluator permissionEvaluator() {
		return new AclPermissionEvaluator(aclService());
	}

	@Bean
	protected PermissionCacheOptimizer aclPermissionCacheOptimizer() {
		return new AclPermissionCacheOptimizer(aclService());
	}
	
	@Bean
	public MethodSecurityExpressionHandler expressionHandler() {
		
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(permissionEvaluator());
		expressionHandler.setPermissionCacheOptimizer(aclPermissionCacheOptimizer());
		return expressionHandler;
	}
}

