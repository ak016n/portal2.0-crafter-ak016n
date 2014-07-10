package com.att.developer.config;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;

@Configuration
@EnableTransactionManagement

public class AppContext {

	
	private static Logger logger = Logger.getLogger(AppContext.class);
	
	private DataSource getJNDIdataSource() {
		DataSource dataSource = null;
		try {
			Context ctx = new InitialContext();
			dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/devcore");
		} catch (NamingException e) {
			logger.error(e);
			new RuntimeException(e);
		}
		return dataSource;
	}
	
	private ConnectionFactory getJMSBroker() {
		ConnectionFactory connectionFactory = null;
		try {
			Context ctx = new InitialContext();
			connectionFactory = (ConnectionFactory) ctx.lookup("java:comp/env/jms/ConnectionFactory");
		} catch (NamingException e) {
			logger.error(e);
			new RuntimeException(e);
		}
		return connectionFactory;
	}
	
    @Bean
    public DataSource dataSource() {
        return getJNDIdataSource();
    }
    
    @Bean
    public UserTransactionService userTransactionService() {
    	UserTransactionServiceImp userTransactionServiceImp = new UserTransactionServiceImp();
    	Properties properties = new Properties();
    	properties.put("com.atomikos.icatch.serial_jta_transactions", "false");
    	properties.put("com.atomikos.icatch.enable_logging", "false");
    	properties.put("com.atomikos.icatch.log_base_dir", "/tx"); // default, need to be configurable to SAN in prod
    	userTransactionServiceImp.init(properties);
    	return userTransactionServiceImp;
    }
    
	@Bean
	@DependsOn("userTransactionService")
	public PlatformTransactionManager txManager() throws Throwable {
		AtomikosJtaPlatform jtaPlatform = this.atomikosJtaPlatform();
		JtaTransactionManager transactionManager = new JtaTransactionManager();
		transactionManager.setTransactionManager(jtaPlatform.getAtomikosTransactionManager());
		transactionManager.setUserTransaction(jtaPlatform.getAtomikosUserTransaction());
		return transactionManager;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setDataSource(dataSource());
		localContainerEntityManagerFactoryBean.setPackagesToScan("com.att.developer.bean");

		JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);

		localContainerEntityManagerFactoryBean.setJpaProperties(additionalProperties());
		return localContainerEntityManagerFactoryBean;
	}

	@Bean
	@DependsOn({"userTransactionService","txManager"})
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	Properties additionalProperties() {
		return new Properties() {
			private static final long serialVersionUID = 4240657154170582110L;
			{
				setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
				setProperty("hibernate.transaction.jta.platform", "com.att.developer.config.AtomikosJtaPlatform");
				setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.CMTTransactionFactory");
				setProperty("hibernate.current_session_context_class", "jta");
			}
		};
	}

	@Bean
	public AtomikosJtaPlatform atomikosJtaPlatform(){
		return new AtomikosJtaPlatform();
	}

    @Bean
    public ConnectionFactory connectionFactory() {
    	return getJMSBroker();
    }
	
	@Bean
	@DependsOn({"connectionFactory"})
	public JmsTemplate jmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setConnectionFactory(connectionFactory());
		return jmsTemplate;
	}
	
}
