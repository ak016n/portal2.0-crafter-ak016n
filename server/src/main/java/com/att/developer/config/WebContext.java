package com.att.developer.config;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.att.developer.exception.TimeoutDeferredResultProcessingInterceptor;
import com.att.developer.service.impl.LocaleAwareResourceBundleMessageSource;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.google.common.base.Predicate;

@Configuration
@EnableWebMvc
@EnableSwagger2
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
	
	 @Bean
	  public Docket swaggerSpringMvcPlugin() {
	    return new Docket(DocumentationType.SWAGGER_2)
	          //  .groupName("business-api")
	            .select() 
	              .paths(paths()) // and by paths
	              .build()
	            .apiInfo(apiInfo())
	            .securitySchemes(securitySchemes());
	          //.securityContexts(securityContext());
	  }


	private List<? extends SecurityScheme> securitySchemes() {
		return Arrays.asList(new OAuth("oauth2", Arrays.<AuthorizationScope>asList(new AuthorizationScope("read", "ability to read")), Arrays.<GrantType>asList(new GrantType("password"))));
	}
	  
	private Predicate<String> paths() {
	    return or(
	        regex("/uauth/.*"),
	        regex("/comgw/.*")
	    	);
	  }
	  
		private ApiInfo apiInfo() {
	        ApiInfo apiInfo = new ApiInfo(
	                "Portal 2.0 APIs",
	                "APIs that are exposed by developer portal",
	                "1.0",
	                "Portal 2.0 terms of service",
	                "ss380m@att.com",
	                "Portal 2.0",
	                ""
	          );
	        return apiInfo;
	   }
}
