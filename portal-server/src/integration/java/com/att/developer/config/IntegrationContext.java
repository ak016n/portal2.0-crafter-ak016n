package com.att.developer.config;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.att.developer.dao"})
public class IntegrationContext {

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:xe");
		//dataSource.setUrl("jdbc:oracle:thin:@devpgm-db.eng.mobilephone.net:1522:DPCI");
//		dataSource.setUrl("jdbc:oracle:thin:@dfw3-npe17-portal-db1.dfw3:1521:XE");
		 
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		dataSource.setUsername("dev_core");
		dataSource.setPassword("dev_core");
		return dataSource;
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
	public PlatformTransactionManager txManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	Properties additionalProperties() {
		return new Properties() {
			private static final long serialVersionUID = 4240657154170582110L;
			{ // Hibernate Specific:
				//setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
            	setProperty("hibernate.dialect","org.hibernate.dialect.Oracle10gDialect");
				setProperty("hibernate.show_sql", "true");
			}
		};
	}
}
