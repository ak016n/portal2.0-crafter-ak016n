package com.att.developer.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import com.att.developer.docs.ClientCredentialGrant;
import com.att.developer.exception.TimeoutDeferredResultProcessingInterceptor;
import com.att.developer.service.impl.LocaleAwareResourceBundleMessageSource;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.models.dto.AuthorizationType;
import com.mangofactory.swagger.models.dto.GrantType;
import com.mangofactory.swagger.models.dto.OAuth;
import com.mangofactory.swagger.models.dto.TokenEndpoint;
import com.mangofactory.swagger.models.dto.builder.OAuthBuilder;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan({ "com.att.developer.controller" })
public class WebContext extends WebMvcConfigurerAdapter {
	
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(60*5000L);
		configurer.registerDeferredResultInterceptors(new TimeoutDeferredResultProcessingInterceptor());
	}
	
	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/");
		return viewResolver;
	}
	
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("language");
		return localeChangeInterceptor;
	}
    
	@Bean(name = "localeResolver")
	public LocaleResolver sessionLocaleResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("en"));
		return localeResolver;
	}
     
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
     
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new LocaleAwareResourceBundleMessageSource();
		messageSource.setBasename("/WEB-INF/i18n/messages");
		return messageSource;
	}
	
    
	 @SuppressWarnings("unchecked")
	 @Override
	 public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
	    builder.indentOutput(true).modulesToInstall(Hibernate4Module.class);
	    converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
	 }
    
	/**
	 * SWAGGER - API Doc Tool
	 */
	
	private SpringSwaggerConfig springSwaggerConfig;

	@Autowired
	public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
		this.springSwaggerConfig = springSwaggerConfig;
	}

	@Bean
	public SwaggerSpringMvcPlugin generalServices() {
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(
				apiInfo()).includePatterns("/comgw/.*","/uauth/.*").swaggerGroup("apis").authorizationTypes(authorizationTypes());
	}
	
	private List<AuthorizationType> authorizationTypes() {
		List<AuthorizationType> authorizationTypes = new ArrayList<>();

		List<GrantType> grantTypes = new ArrayList<>();
	    TokenEndpoint tokenEndpoint = new TokenEndpoint("/developer/oauth/token", "access_token");

	    ClientCredentialGrant authorizationCodeGrant = new ClientCredentialGrant(tokenEndpoint);
	    grantTypes.add(authorizationCodeGrant);

	    OAuth oAuth = new OAuthBuilder()
	            //.scopes(authorizationScopeList)
	            .grantTypes(grantTypes)
	            .build();

	    authorizationTypes.add(oAuth);
	    return authorizationTypes;
		
	}
	
	/*
	 * Alternate Swagger initialization
 
	    @Bean
		public SwaggerSpringMvcPlugin restrictedServices() {
			return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(
					apiInfo()).includePatterns("/uauth/.*").swaggerGroup("restricted").authorizationContext(authorizationContext());
		}
	
	
		
	    private AuthorizationContext authorizationContext() {
	    	Authorization authorization = new Authorization("oauth2", new AuthorizationScope[]{ new AuthorizationScope("read", "ability to read")});
			return new AuthorizationContextBuilder(Arrays.asList(new Authorization[] {authorization})).build();
		}
   */

	private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Portal 2.0 APIs",
                "APIs that are exposed by developer portal",
                "Portal 2.0 terms of service",
                "ss380m@att.com",
                "Portal 2.0 Licence Type",
                "Portal 2.0 License URL"
          );
        return apiInfo;
   }
}
