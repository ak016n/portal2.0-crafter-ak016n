package com.att.developer.config;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.att.developer.exception.TimeoutDeferredResultProcessingInterceptor;
import com.att.developer.service.impl.LocaleAwareResourceBundleMessageSource;

@Configuration
@EnableWebMvc
@ComponentScan({ "com.att.developer.controller" })
public class WebContext extends WebMvcConfigurerAdapter {
	
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		//configurer.setDefaultTimeout(60*5000L);
		configurer.setDefaultTimeout(60*10L);
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
}
