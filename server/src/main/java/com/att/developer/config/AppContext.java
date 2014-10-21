package com.att.developer.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.sql.DataSource;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.interceptor.JamonPerformanceMonitorInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.jamonapi.MonitorFactory;

@Configuration
@EnableTransactionManagement
@PropertySource(value="file:${java:comp/env/configLocation}/performanceMonitoring.properties", ignoreResourceNotFound=true)
@ComponentScan({"com.att.developer"})
public class AppContext {

    private final Logger logger = LogManager.getLogger();

    
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);
    
    /**
     * Defined to turn on or off the JAMON performance monitoring.
     * Default is turned off.  The property file with settings are
     * kept in a JNDI defined location
     * 
     */
    @Value("${performanceMonitoringOn:false}")
    private String performanceMonitoringOn; 
    
    
    /**
     * Beans which JAMON will wrap with intercepter and monitor for performance.
     * 
     */
    @Value("${beansToMonitor:noService}")
    private String[] beansToMonitorForPerformance;

    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    private DataSource getJNDIdataSource() {
        DataSource dataSource = null;
        try {
            //Context ctx = new InitialContext();
        	//ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
        	//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        	InitialContext ctx = new InitialContext();
            //JndiTemplate jndi = new JndiTemplate();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/devcore");
        } catch (NamingException e) {
            logger.error(e);
            new RuntimeException(e);
        }
        return dataSource;
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
    @DependsOn({"userTransactionService", "txManager"})
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
    public AtomikosJtaPlatform atomikosJtaPlatform() {
        return new AtomikosJtaPlatform();
    }

    @Bean
    public TransactionTemplate transactionTemplate() throws Throwable {
        return new TransactionTemplate(this.txManager());
    }
    
    @Bean
    public JamonPerformanceMonitorInterceptor jamonPerformanceMonitorInterceptor(){
        JamonPerformanceMonitorInterceptor jamonInterceptor = new JamonPerformanceMonitorInterceptor();
        jamonInterceptor.setUseDynamicLogger(true);
        jamonInterceptor.setHideProxyClassNames(true);

        //use this to force all events to log despite Log4j2 settings.
        //jamonInterceptor.setTrackAllInvocations(false);
        
        return jamonInterceptor;
    }
    

    @Bean
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
        logger.info("is performanceMonitoringOn ?  " + performanceMonitoringOn);
        logger.info("list of beansToMonitor " + Arrays.toString(beansToMonitorForPerformance));
        
        boolean perfMonitoringOn = Boolean.valueOf(performanceMonitoringOn);

        BeanNameAutoProxyCreator proxyCreator = new BeanNameAutoProxyCreator();
        if (perfMonitoringOn) {
            String[] interceptorNames = {"jamonPerformanceMonitorInterceptor"};
            proxyCreator.setInterceptorNames(interceptorNames);
            proxyCreator.setBeanNames(beansToMonitorForPerformance);
        }
        return proxyCreator;
    }    
    
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
/*		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();

		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
				jsonConverter.setObjectMapper(objectMapper);
			}
		}*/

    return restTemplate;
    }
	
/*	@Bean
	public HttpClient httpClient() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		CloseableHttpClient defaultHttpClient = HttpClients.custom().setSSLSocketFactory(getSSLSocketFactory()).setConnectionManager(connectionManager).build();

		connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
		
		return defaultHttpClient;
	}
	
    private SSLConnectionSocketFactory getSSLSocketFactory() {
      try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {// NOPMD
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {// NOPMD
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[] {tm}, null);
            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx, new AllowAllHostnameVerifier());
            return ssf;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }*/
    
	@Bean
	public HttpClient httpClient() {
      SSLContextBuilder builder = SSLContexts.custom();
		try {
			builder.loadTrustMaterial(null, new TrustStrategy() {
			    @Override
			    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			        return true;
			    }
			});
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}

		SSLContext sslContext = null;
		try {
			sslContext = builder.build();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();

		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		
		connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
		
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
		
		return httpClient;
	}
	
	@Bean
	public 	ClientHttpRequestFactory httpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient());
		return clientHttpRequestFactory;
	}
    
    @PreDestroy
    public void destroy(){
        logger.info("Developer shutting down destroy / cleanup ");
        
        
        boolean perfMonitoringOn = Boolean.valueOf(performanceMonitoringOn);
        if(perfMonitoringOn){
            Logger performanceLogger = LogManager.getLogger("performance");
            String htmlJamonReport = MonitorFactory.getReport();
            performanceLogger.warn("jamon html report at shutdown \n\n " + htmlJamonReport);
        }
    }
}