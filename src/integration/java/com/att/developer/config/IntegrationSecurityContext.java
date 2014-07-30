package com.att.developer.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import net.sf.ehcache.CacheManager;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.att.developer.bean.Role;
import com.att.developer.dao.ApiBundleDAO;
import com.att.developer.security.CustomAclLookupStrategy;
import com.att.developer.security.CustomPermissionGrantingStrategy;
import com.att.developer.security.EventLogAuditLogger;
import com.att.developer.security.PermissionManager;
import com.att.developer.security.PermissionManagerImpl;
import com.att.developer.service.ApiBundleService;
import com.att.developer.service.EventTrackingService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.OrganizationService;
import com.att.developer.service.UserService;
import com.att.developer.service.impl.ApiBundleServiceImpl;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
@ComponentScan({"com.att.developer.service.impl.ApiBundleServiceImpl"})
public class IntegrationSecurityContext extends WebSecurityConfigurerAdapter{

    @Inject
    private DataSource dataSource;
    
    
    @Inject
    private PlatformTransactionManager txManager;
    
    @Inject 
    private ApiBundleDAO apiBundleDAO;
    

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("somas").password("password123").roles("ADMIN")
                .and()
                .withUser("user2").password("password123").roles("USER");

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
    public EhCacheBasedAclCache aclCache() {
        EhCacheBasedAclCache aclCache = new EhCacheBasedAclCache(ehCacheFactoryBean().getObject(), permissionGrantingStrategy(), aclAuthorizationStrategy());
        return aclCache;
    }

    @Bean
    public CustomAclLookupStrategy basicLookupStrategy(DataSource dataSource) {
        return new CustomAclLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), permissionGrantingStrategy());
    }

    @Bean
    public JdbcMutableAclService aclService(DataSource dataSource) {
        JdbcMutableAclService aclService = new JdbcMutableAclService(dataSource, basicLookupStrategy(dataSource), aclCache());

        aclService.setClassIdentityQuery("SELECT LAST_INSERT_ID()");
        aclService.setSidIdentityQuery("SELECT LAST_INSERT_ID()");

        return aclService;
    }

    @Bean
    protected EhCacheFactoryBean ehCacheFactoryBean() {

        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        CacheManager cacheManager = CacheManager.create();
        ehCacheFactoryBean.setCacheManager(cacheManager);
        ehCacheFactoryBean.setCacheName("aclCache");

        return ehCacheFactoryBean;

    }

    @Bean
    protected PermissionGrantingStrategy permissionGrantingStrategy() {
        return new CustomPermissionGrantingStrategy(auditLogger());
    }

    @Bean
    protected EventLogAuditLogger auditLogger() {
        return new EventLogAuditLogger();
    }

    @Bean
    protected EventTrackingService eventTrackingService() {
        return Mockito.mock(EventTrackingService.class);
    }

    @Bean
    protected AclAuthorizationStrategy aclAuthorizationStrategy() {
        // controls who can grant and remove permissions
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority(Role.ROLE_NAME_SYS_ADMIN));
    }

    @Bean
    public PermissionManager permissionManager() {
        PermissionManager mgr = new PermissionManagerImpl(this.txManager, 
                                    aclService(dataSource), 
                                    transactionTemplate(), 
                                    dataSource, 
                                    jdbcTemplate(), 
                                    organizationService(), 
                                    userService(), 
                                    globalScopedParamService());
        
        return mgr;
    }

    @Bean
    public OrganizationService organizationService() {
        return Mockito.mock(OrganizationService.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public GlobalScopedParamService globalScopedParamService() {
        return Mockito.mock(GlobalScopedParamService.class);
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    protected PermissionEvaluator permissionEvaluator(DataSource ds) {
        return new AclPermissionEvaluator(aclService(ds));
    }

    @Bean
    protected PermissionCacheOptimizer aclPermissionCacheOptimizer(DataSource ds) {
        return new AclPermissionCacheOptimizer(aclService(ds));
    }

    @Bean
    public MethodSecurityExpressionHandler expressionHandler(DataSource ds) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator(ds));
        expressionHandler.setPermissionCacheOptimizer(aclPermissionCacheOptimizer(ds));
        return expressionHandler;
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
         return new TransactionTemplate(txManager);
    }
    
    @Bean
    public ApiBundleService apiBundleService(){
        return new ApiBundleServiceImpl(this.apiBundleDAO, permissionManager(), eventTrackingService(), globalScopedParamService());
    }
}