package com.att.developer.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;

import net.sf.ehcache.CacheManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.PermissionCacheOptimizer;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import com.att.developer.bean.Role;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.wrapper.Principal;
import com.att.developer.security.AttPasswordEncoder;
import com.att.developer.security.AuthenticationEnhancementFilter;
import com.att.developer.security.CustomAclLookupStrategy;
import com.att.developer.security.CustomPermissionGrantingStrategy;
import com.att.developer.security.EventLogAuditLogger;
import com.att.developer.security.Oauth2UserApprovalHandler;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableWebMvcSecurity
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

	public static final String CLIENT_NAME = "client_name";
	
    @Inject
    private DataSource dataSource;

    @Resource(name = "attUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new AttPasswordEncoder());
    }
	
    protected void configure(HttpSecurity http) throws Exception {
   	http
            .authorizeRequests()
                .antMatchers("/index.html/**").permitAll()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/i18n").permitAll()
                .anyRequest().authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);	

        http.csrf().disable();
    }
	
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	    
    @Bean
    public EhCacheBasedAclCache aclCache() {
        EhCacheBasedAclCache aclCache = new EhCacheBasedAclCache(ehCacheFactoryBean().getObject(), permissionGrantingStrategy(),
                aclAuthorizationStrategy());
        return aclCache;
    }

    @Bean
    public CustomAclLookupStrategy basicLookupStrategy() {
        return new CustomAclLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), permissionGrantingStrategy());
    }

    @Bean
    public JdbcMutableAclService aclService() {
        JdbcMutableAclService aclService = new JdbcMutableAclService(dataSource, basicLookupStrategy(), aclCache());

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
    protected AclAuthorizationStrategy aclAuthorizationStrategy() {
        // controls who can grant and remove permissions
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority(Role.ROLE_NAME_SYS_ADMIN));
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
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
        OAuth2MethodSecurityExpressionHandler expressionHandler = new OAuth2MethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator());
        expressionHandler.setPermissionCacheOptimizer(aclPermissionCacheOptimizer());
        return expressionHandler;
    }
    
    
    //oauth2 below here ------------------------------------------------------
    
    private static final String RESOURCE_ID = "restservice";
    
    @Configuration
    @EnableResourceServer
    @EnableGlobalMethodSecurity(prePostEnabled=true)
    @EnableWebMvcSecurity
    @EnableWebSecurity
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        
        @Autowired
        private TokenStore tokenStore;

        @Autowired
        private AuthenticationEnhancementFilter authenticationEnhancementFilter;
        
        @Resource(name="attUserDetailsService")
        private UserDetailsService userDetailsService;
        
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(RESOURCE_ID)
                    .tokenStore(tokenStore);
            
        }

        
        /**
         * '/cauth/' context means client only authorization required. 
         * '/uauth/' context means fully user authorization required.
         */
        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .requestMatchers().antMatchers("/admin/**", "/cauth/**", "/uauth/**", "/oauth/revoke/**")
            .and()
                .authorizeRequests()
                    .antMatchers("/admin/**", "**/apiBundle/add/**").hasRole("SYS_ADMIN")
//                                .antMatchers("/eventLog/**").access("#oauth2.hasScope('trust') and #oauth2.clientHasRole('ROLE_INTERNAL_CLIENT')")//no worky, client loses authorities for some reason.
                    .antMatchers("/cauth/**").access("#oauth2.isClient()")
                    .antMatchers("/uauth/**").access("#oauth2.isUser() and #oauth2.hasScope('trust')")

                    .antMatchers("/cauth/eventLog/**").access("#oauth2.hasScope('trust')")
                    .antMatchers("/oauth/revoke/**").access("#oauth2.isClient() and #oauth2.hasScope('trust')")
                    .anyRequest().authenticated()
            .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            // @formatter:on
            http.addFilterAfter(authenticationEnhancementFilter, FilterSecurityInterceptor.class);
        }


    }
    
    @Bean 
    public AuthenticationEnhancementFilter authenticationEnhancementFilter(){
        return new AuthenticationEnhancementFilter();
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
        private TokenStore tokenStore;
        
        @Autowired
        private UserApprovalHandler userApprovalHandler;


        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient("trusted_internal_client_with_user")
//                        .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                        .authorizedGrantTypes("password", "refresh_token")
                        .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                        .scopes("read", "write", "trust")
                        .secret("somesecret_ticwu")
                        .accessTokenValiditySeconds(60*60) //1 hour
                        .refreshTokenValiditySeconds(4*60*60) //4 hours
                        .additionalInformation(new String[] {CLIENT_NAME + ":developer"})
                 .and()
                     //oauth2 spec recommends against refresh tokens for clients. NO REFRESH TOKEN here.
                     .withClient("trusted_internal_client")
                         .authorizedGrantTypes("client_credentials")
                         .authorities("ROLE_INTERNAL_CLIENT")
                         .scopes("read", "write", "trust")
                         .secret("somesecret_tic")   
                         .accessTokenValiditySeconds(60*60) //one hour
            			 .additionalInformation(new String[] {CLIENT_NAME +":EDO"});
        }
        
        @Bean
        public TokenStore tokenStore() {
            return new JwtTokenStore(accessTokenConverter());
        }
        
        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
            JwtAccessTokenConverter converter = new SessionInfoEnhancer();
            converter.setSigningKey(RSA_PRIVATE_SIGNING_KEY);
            converter.setVerifierKey(RSA_PUB_VERIFIER_KEY);
            return converter;
        }
        
        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;
        
        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                    .tokenStore(tokenStore)
                    .userApprovalHandler(userApprovalHandler)
                    .authenticationManager(authenticationManager)
                    .accessTokenConverter(accessTokenConverter());
        }
        
       private static class SessionInfoEnhancer extends JwtAccessTokenConverter {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                final Map<String, Object> additionalInfo = new HashMap<>();
                if(authentication.getPrincipal() instanceof SessionUser) {
                	additionalInfo.put("principal", new Principal((SessionUser) authentication.getPrincipal()));
                }
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
                return super.enhance(accessToken, authentication);
            }
        }
    
        private static final String RSA_PUB_VERIFIER_KEY = "-----BEGIN RSA PUBLIC KEY-----"
                +"MIICCgKCAgEAtTeKhfS1qDKMdkzuOls+/dQKzzV8J+NSCx5+wIyDpUDjRQVYdPsx"
                +"ONsr3C5E3JrJbuvHNZN5lL4kwadB4g+g5QN8gq0FW6HnYhWIoeqJRTl9UDbBvu9K"
                +"JCEOwwrtHaF8rJkiqDYbUXbSwHTVb1g0pQNKQW6VZRxwYXZDPe+x7ULO0P0IvMZz"
                +"kOLSyvnP3i2SM0y5MOq7SxxibpTe7jbgbWMZ4Ad/Ah4kEr5tik4oZrBG7R5BUdc4"
                +"3xfpKSlCKRJFg7Ml/bs1NIarMNMNnwlF0BBtCMloZVypVDLIu8kJhjZfIIyQUZhB"
                +"pfPrrTLYktddAaj6vKNsEBl7ii+egqLvy98aCWa/Etv2i2oC2Nq2hp7htXdlcDAu"
                +"DV6MjHrFxiE5UBM5qtPNnwFpkz57dObOsYfFwWft4sobL+UfEdc0R05tunztA5Ao"
                +"SXDQ8hcQz+ua+YqIb/tg6h4bhdBg0XcfuCnI8VbKSqZHJnYOh02bUKpRmxXWnYsZ"
                +"+rPzFlLR6o6R1vshknr6CXZ88f71POAYs2lC1e+Z8dxpJwVTiRx2HAXYWIEZlu2T"
                +"DcNLrwKvvWNuGYSZwTYtanJPI4k3mzoRP08RyCptdhb60VYMONyvLFfwarxBYD6n"
                +"m0ibMzdRz1FRZViKwTrUu67eqqXLS59wBup71lSStdLOFiP6yBMhcPkCAwEAAQ=="
                +"-----END RSA PUBLIC KEY-----";
        
        private static final String RSA_PRIVATE_SIGNING_KEY = "-----BEGIN RSA PRIVATE KEY-----"
                +"MIIJKAIBAAKCAgEAtTeKhfS1qDKMdkzuOls+/dQKzzV8J+NSCx5+wIyDpUDjRQVY"
                +"dPsxONsr3C5E3JrJbuvHNZN5lL4kwadB4g+g5QN8gq0FW6HnYhWIoeqJRTl9UDbB"
                +"vu9KJCEOwwrtHaF8rJkiqDYbUXbSwHTVb1g0pQNKQW6VZRxwYXZDPe+x7ULO0P0I"
                +"vMZzkOLSyvnP3i2SM0y5MOq7SxxibpTe7jbgbWMZ4Ad/Ah4kEr5tik4oZrBG7R5B"
                +"Udc43xfpKSlCKRJFg7Ml/bs1NIarMNMNnwlF0BBtCMloZVypVDLIu8kJhjZfIIyQ"
                +"UZhBpfPrrTLYktddAaj6vKNsEBl7ii+egqLvy98aCWa/Etv2i2oC2Nq2hp7htXdl"
                +"cDAuDV6MjHrFxiE5UBM5qtPNnwFpkz57dObOsYfFwWft4sobL+UfEdc0R05tunzt"
                +"A5AoSXDQ8hcQz+ua+YqIb/tg6h4bhdBg0XcfuCnI8VbKSqZHJnYOh02bUKpRmxXW"
                +"nYsZ+rPzFlLR6o6R1vshknr6CXZ88f71POAYs2lC1e+Z8dxpJwVTiRx2HAXYWIEZ"
                +"lu2TDcNLrwKvvWNuGYSZwTYtanJPI4k3mzoRP08RyCptdhb60VYMONyvLFfwarxB"
                +"YD6nm0ibMzdRz1FRZViKwTrUu67eqqXLS59wBup71lSStdLOFiP6yBMhcPkCAwEA"
                +"AQKCAgBAHgqxIsgzXs87/DQ+CZLcFG5OqknngxLARGXsksrxaWgAP8fwfAAKceGu"
                +"4eATfeDPkjTFzAw0iKJQsnEpHwZ6gSVIxxciOMK5fYz+XRF8oL6p5vXeLKQ67Edg"
                +"0zjaRwzptLUCd0JrLHOdDLmHz9mwmN1pEUtinxFUKpfYDjsSC5VJdH3m7QBuvxJ+"
                +"Jq1Zmx4jcH4FxMfH558cKShHeLsneOzqyzww76Er0JzJVqYMz0oJE69g2ZmRtdAV"
                +"dDeoeIKH+pbyk45bq1peyA4Fuy4sU+OAPGLnmkV/OQi1CIiKKCq4RYO6mvh/UzKL"
                +"8XWXXwPVVoiAvgG6IIoJdpfK+QOGslkDu7xNGAEhKvEegcaQRkfHjiNIHmrpx/8e"
                +"99jQ9+utMAFaWfuePGhKQupShm0YaP23hdeSv3XvWsX6v6bNlP4oTsHGyr9PGVOt"
                +"dv5jxXjhGZ2WokcpQLFzWBIX1PsgQePvguD6Mdc38OwuDESdJkmOtTCwOa/tFHd+"
                +"peztPjNG9o3hNkbTbO733TT7betR1kR3NA4XWQQjzuDK+yzJbkIAR6rm5KwQfdwl"
                +"YG3DRRc67y9sc2scVsRtg5p2exeeSrL+RQ+d186/Njg1bdOBKQABfgXy3XNiPQ5y"
                +"WSewsyxyKyaUak4rTiLfBbmys1y+4X+Ja9+DxIgWjKJa9PF6AQKCAQEA2WyuF+OZ"
                +"5kq97QIihtxSlhSXtLPWIfyqGYyp3Rlf4/5DbnaaG7v9DYAbm7T7CM9bNnY707jM"
                +"Pb4oOnkX4zssx4ZP9kZliIydJLLzvyoswyduJ/np2gTLGrctLexQXRCur86MA3np"
                +"300MeL7HJgaHReOhJTrRddxci2ZrzhKmYARPATDcmD6AAgeIF9pg9JLGRMpAeKWk"
                +"6imBagEx+eESd8a/upiNJGsdqBH7G3ncDft/7l1xfq2v3aH4rNFSBPVuvrehw4sl"
                +"oyvTmU3XQvU0UH4lPySXeD3p1mhxWaooXG4xVTuOKuKMtvPcG/e3aZGw9dzbByB3"
                +"SIvwId7+NwwuKQKCAQEA1V5Uf74vS83hwd5Lg8yUQFraIRQC39W1MyYNMsM4s4e+"
                +"kiRp7l1o/C+ymWgh+p4m9EjZDDPBueK3uk+JdSBOBUuDpLOKEDI9quZR9FHq4Gr+"
                +"ODiVlorvzpcK2cXOLC0Yyf7L4T3GRZH+slNAK/fHjLfvREzHJ6dwzQrBPuO1SYeD"
                +"L86e9unjD03WOil4IPiz785T7KDjMOsdNHbt2y1AuOk0sEzbKBz5I1gMtEIpBPWJ"
                +"TxxXz52VjaV6994o0ghpsBbtNo0j5ogfuvvQEXrrIOoNVoWoA67lgbw8gTQyCApn"
                +"MyCSCtbJN/hwjsO4dGgBGRI1g0/kJHgq0FRRnh7mUQKCAQANC3Q2grVNF9blTWd8"
                +"5+Mclge0E8YZK+uYtNPMLCWQrESnb/43A8Re9vyxDt6w3KAAWH+maEP4wFvxhipk"
                +"u7woDGrnv7l/w8/5z7LIGWOuIcN+KoRZTEhTRgIz6yW3L7ULXz0PSFU7zIefBvMz"
                +"2Nhs3QdiEHFYvOvggTiemIfa4udKjv/tMnLDrGgB5lxC0DuNDQVpN6b5VZwqHAA/"
                +"mb1d6lK+g0CUmsiguNN0Bo+pVxmJ15ljnFAc4Abnd3eQTJlkX+gYr1SGwa5kWEM+"
                +"BTAqVCcv7qYX1L8e2g1S796UDRhwoK4a7JS8tUzTcL1UXCVd2u5ZMx11ANBbd+GQ"
                +"s+5hAoIBAQCByCHPnaf5DN7P5F28aliWmGxriFeDPOM9nFyUzugTU3MYQ2vnoAu2"
                +"sXCKHSvl3ALHvfO/l0+zuHKWscBjbOXoFJmbOdW1qkmWRQf+e0FJherh2ZErg5D/"
                +"8SGZetbASiH047WxaLjMIlRwtXCoDcvkDRFNUwIkXZWgMnV+wlTHx9SE5MIOYA9L"
                +"hMXUlNrL+1q/d5nWqbnnTGt0OQ7OrUTb9IWm4ui/CniEBg+cKU34BT2i0BOdZjXM"
                +"daZJVGkx8AoRILDi6JC4rX3XZ0mVCFYyrAq24Q9kRiK1egC3ej8gYuuIzrvmOeUk"
                +"1UnpnKT6sTRRwqCoIZUclbc9BryqWBMxAoIBAEpwZlzWcvtKYdOEnJkjjrPfys8Q"
                +"44Nf8nwtOZCcPu4kY/8vsfF6R3MhNSiYXE8t6rvuVJi24LlwHLDcUcfo0fXFMrAm"
                +"0psLxkP/MnwWJDzvmggdONdmGdN12C5HKq37rKanG6VUmWpW6LMctrKsoPqPE02s"
                +"3rgn2i8LmUqh6gx8zHp7HyKM/+UKFNq9t1A35bXph4XRKyf9jZz0XXtw2rO3rtGG"
                +"24akwLsw4U1nb7uUoEp0Dboyq5WfCbdEg8Gg0fWPf4FQP0R1FRsS9izhOcNB5x6o"
                +"gKnDiCLnv3WL3otBJ1RplrbT6G1DS8bCc9/VUWEtvktOCWd1HCY+FKynjQA="
                +"-----END RSA PRIVATE KEY-----";
    }
    

    protected static class Stuff {

        @Autowired
        private ClientDetailsService clientDetailsService;

        @Autowired
        private TokenStore tokenStore;
        

        @Bean
        public ApprovalStore approvalStore() throws Exception {
            TokenApprovalStore store = new TokenApprovalStore();
            store.setTokenStore(tokenStore);
            return store;
        }

        @Bean
        @Lazy
        @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
        public Oauth2UserApprovalHandler userApprovalHandler() throws Exception {
            Oauth2UserApprovalHandler handler = new Oauth2UserApprovalHandler();
            handler.setApprovalStore(approvalStore());
            handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
            handler.setClientDetailsService(clientDetailsService);
            handler.setUseApprovalStore(true);
            return handler;
        }
    }
}

