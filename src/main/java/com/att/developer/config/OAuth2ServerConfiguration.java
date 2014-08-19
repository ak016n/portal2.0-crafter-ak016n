package com.att.developer.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.att.developer.security.Oauth2UserApprovalHandler;

//@Configuration
public class OAuth2ServerConfiguration {

//    private static final String RESOURCE_ID = "restservice";
//    
//    @Autowired
//    @Qualifier("authenticationManagerBean")
//    private AuthenticationManager authenticationManager;
    
//    @Configuration
//    @Order(10)
//    protected static class UiResourceConfiguration extends WebSecurityConfigurerAdapter {
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http
//                .requestMatchers().antMatchers("/photos/**","/me")
//                .and()
//                .authorizeRequests()
//                .antMatchers("/me").access("hasRole('ROLE_USER')")
//                .antMatchers("/photos").access("hasRole('ROLE_USER')")
//                .antMatchers("/photos/trusted/**").access("hasRole('ROLE_USER')")
//                .antMatchers("/photos/user/**").access("hasRole('ROLE_USER')")
//                .antMatchers("/photos/**").access("hasRole('ROLE_USER')");
//
//        }
//    }

    
    
//    @Configuration
//    @EnableResourceServer
//    @EnableGlobalMethodSecurity(prePostEnabled=true)
//    @EnableWebMvcSecurity
//    @EnableWebSecurity
//    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
//
//        
//        @Autowired
//        private TokenStore tokenStore;
//        
//        @Resource(name="attUserDetailsService")
//        private UserDetailsService userDetailsService;
//        
//        @Override
//        public void configure(ResourceServerSecurityConfigurer resources) {
//            resources.resourceId(RESOURCE_ID)
//                    .tokenStore(tokenStore);
//            
//        }
//
//        
//        /**
//         * '/cauth/' context means client only authorization required. 
//         * '/uauth/' context means fully user authorization required.
//         */
//        @Override
//        public void configure(HttpSecurity http) throws Exception {
//                // @formatter:off
//                http
//                        .requestMatchers().antMatchers("/photos/**", "/oauth/users/**", "/oauth/clients/**","/me", "/admin/**","/eventLog/**", "/cauth/**", "/uauth/**")
//                        
//                .and()
//                        .authorizeRequests()
//                                .antMatchers("/views/home.html").permitAll()
//                                .antMatchers("/resources/**").permitAll()
//                                .antMatchers("/admin/**", "/views/adminConsole/**", "**/apiBundle/add/**").hasRole("SYS_ADMIN")
////                                .antMatchers("/eventLog/**").access("#oauth2.hasScope('trust') and #oauth2.clientHasRole('ROLE_INTERNAL_CLIENT')")//no worky, client loses authorities for some reason.
//                                .antMatchers("/cauth/**").access("#oauth2.isClient()")
//                                .antMatchers("/uauth/**").access("#oauth2.isUser() and #oauth2.hasScope('trust')")
//
//                                .antMatchers("/cauth/eventLog/**").access("#oauth2.hasScope('trust')")
//                                .anyRequest().authenticated()
//                                //below is taken from sample spring oauth    
//                                .antMatchers("/me").access("#oauth2.hasScope('read')")
//                                .antMatchers("/photos").access("#oauth2.hasScope('read')")
//                                .antMatchers("/photos/trusted/**").access("#oauth2.hasScope('trust')")
//                                .antMatchers("/photos/user/**").access("#oauth2.hasScope('trust')")
//                                .antMatchers("/photos/**").access("#oauth2.hasScope('read')")
//                                .regexMatchers(HttpMethod.DELETE, "/oauth/users/([^/].*?)/tokens/.*")
//                                        .access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('write')")
//                                .regexMatchers(HttpMethod.GET, "/oauth/clients/([^/].*?)/users/.*")
//                                        .access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('read')")
//                                .regexMatchers(HttpMethod.GET, "/oauth/clients/.*")
//                                        .access("#oauth2.clientHasRole('ROLE_CLIENT') and #oauth2.isClient() and #oauth2.hasScope('read')");
//                // @formatter:on
//                http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        }
//
//    }
//
//    @Configuration
//    @EnableAuthorizationServer
//    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
//
//        @Autowired
//        private TokenStore tokenStore;
//        
//        @Autowired
//        private UserApprovalHandler userApprovalHandler;
//
//
//        @Override
//        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//            clients.inMemory()
//                    .withClient("trusted_internal_client_with_user")
////                        .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
//                        .authorizedGrantTypes("password", "refresh_token")
//                        .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
//                        .scopes("read", "write", "trust")
//                        .secret("somesecret_ticwu")
//                        .accessTokenValiditySeconds(300)
//                        .refreshTokenValiditySeconds(60*60) //one hour
//                 .and()
//                     .withClient("trusted_internal_client")
//                         .authorizedGrantTypes("client_credentials", "refresh_token")
//                         .authorities("ROLE_INTERNAL_CLIENT")
//                         .scopes("read", "write", "trust")
//                         .secret("somesecret_tic")
//                         .accessTokenValiditySeconds(60*60) //one hour
//                         .refreshTokenValiditySeconds(24*60*60);
//            
//            
//        }
//        
//        @Bean
//        public TokenStore tokenStore() {
////                return new InMemoryTokenStore();
//            return new JwtTokenStore(accessTokenConverter());
//        }
//        
//        @Bean
//        public JwtAccessTokenConverter accessTokenConverter() {
//                return new JwtAccessTokenConverter();
//        }
//        
//        @Autowired
//        @Qualifier("authenticationManagerBean")
//        private AuthenticationManager authenticationManager;
//        
//        @Override
//        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//            endpoints
//                    .tokenStore(tokenStore)
//                    .userApprovalHandler(userApprovalHandler)
//                    .authenticationManager(authenticationManager)
//                    .accessTokenConverter(accessTokenConverter());
//        }
//    
////        @Override
////        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
////                oauthServer.realm("sparklr2/client");
////        }
//    }
//    
//    
//    protected static class Stuff {
//
//        @Autowired
//        private ClientDetailsService clientDetailsService;
//
//        @Autowired
//        private TokenStore tokenStore;
//
//        @Bean
//        public ApprovalStore approvalStore() throws Exception {
//            TokenApprovalStore store = new TokenApprovalStore();
//            store.setTokenStore(tokenStore);
//            return store;
//        }
//
//        @Bean
//        @Lazy
//        @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
//        public Oauth2UserApprovalHandler userApprovalHandler() throws Exception {
//            Oauth2UserApprovalHandler handler = new Oauth2UserApprovalHandler();
//            handler.setApprovalStore(approvalStore());
//            handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
//            handler.setClientDetailsService(clientDetailsService);
//            handler.setUseApprovalStore(true);
//            return handler;
//        }
//    }
    
}
