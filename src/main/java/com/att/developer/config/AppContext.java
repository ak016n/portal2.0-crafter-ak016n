package com.att.developer.config;

import java.util.Properties;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@EnableTransactionManagement
public class AppContext {

	@Inject
	private AtomikosJtaPlatform jtaPlatform;
	
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
	
    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource dataSource() {
        return getJNDIdataSource();
    }
	
	@Bean
	public PlatformTransactionManager txManager() throws Throwable {
		JtaTransactionManager transactionManager = new JtaTransactionManager();
		transactionManager.setTransactionManager(jtaPlatform.getJNDITransactionManager());
		transactionManager.setUserTransaction(jtaPlatform.getJNDIUserTx());
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

	/**
	 * <bean id="setMyAtomikosSystemProps"  class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
        <property name="targetObject">
            <!-- System.getProperties() -->
            <bean class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
                <property name="targetClass" value="java.lang.System" />
                <property name="targetMethod" value="getProperties" />
            </bean>
        </property>
        <property name="targetMethod" value="putAll" />
        <property name="arguments">
            <!-- The new Properties -->
            <util:properties>
                <prop key="com.atomikos.icatch.file">/etc/myapp/jta.properties</prop>
                <prop key="com.atomikos.icatch.hide_init_file_path">true</prop>
            </util:properties>
        </property>
    </bean>
	 */
/*	
	@Bean
	public MethodInvokingFactoryBean setAtomikosSystemProperties() {
		
		MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
		
		MethodInvokingFactoryBean innerBean = new MethodInvokingFactoryBean();
		innerBean.setTargetClass(java.lang.System.class);
		innerBean.setTargetMethod("getProperties");
		
		factoryBean.setTargetObject(innerBean);
		factoryBean.setTargetMethod("putAll");
		
		Properties properties = new Properties();
		properties.put("com.atomikos.icatch.file", "/jta.properties");
		properties.put("com.atomikos.icatch.hide_init_file_path", "true");
		
		factoryBean.setArguments(new Object[]{properties});
		
		return factoryBean;
	}*/
}
