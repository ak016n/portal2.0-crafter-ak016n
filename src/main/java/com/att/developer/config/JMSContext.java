package com.att.developer.config;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import com.att.developer.jms.consumer.EventLogConsumer;
import com.att.developer.jms.consumer.UserEventConsumer;
import com.att.developer.jms.producer.EventLogProducer;
import com.att.developer.jms.producer.UserEventProducer;

@Configuration
public class JMSContext {
	
    private static final String EVENT_QUEUE_DESTINATION = EventLogProducer.EVENT_QUEUE_DESTINATION;
    private static final String USER_EVENT_TOPIC_DESTINATION = UserEventProducer.USER_EVENT_TOPIC_DESTINATION;
	
    private final Logger logger = LogManager.getLogger();
    
    @Autowired
    private AppContext appContext;
	
    private ConnectionFactory getJmsConnectionFactory() {
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
    public ConnectionFactory connectionFactory() {
        return getJmsConnectionFactory();
    }

    @Bean
    public Queue eventQueue() {
        Queue eventQueue = new ActiveMQQueue(EVENT_QUEUE_DESTINATION);
        return eventQueue;
    }
    
    @Bean
    public Topic userEventTopic() {
        Topic userEventTopic = new ActiveMQTopic(USER_EVENT_TOPIC_DESTINATION);
        return userEventTopic;
    }

    @Bean(destroyMethod = "stop")
    public ConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setMaxConnections(10);
        pooledConnectionFactory.setConnectionFactory(getJmsConnectionFactory());
        return pooledConnectionFactory;
    }

    @Bean
    @DependsOn({"pooledConnectionFactory"})
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean
    public EventLogConsumer eventLogConsumer() {
    	return new EventLogConsumer();
    }
    
    @Bean
    public UserEventConsumer userEventConsumer() {
    	return new UserEventConsumer();
    }
    
    @Bean
    @DependsOn({"txManager", "pooledConnectionFactory"})
    public DefaultMessageListenerContainer eventLogMessageListenerContainer() throws Throwable {
        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(pooledConnectionFactory());
        messageListenerContainer.setDestinationName(EVENT_QUEUE_DESTINATION);
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(eventLogConsumer());
        messageListenerContainer.setMessageListener(messageListenerAdapter);
        messageListenerContainer.setSessionTransacted(true);
        messageListenerContainer.setTransactionManager(appContext.txManager());
        return messageListenerContainer;
    }
    
    @Bean
    @DependsOn({"txManager", "pooledConnectionFactory"})
    public DefaultMessageListenerContainer userEventTopicMessageListenerContainer() throws Throwable {
        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(pooledConnectionFactory());
        messageListenerContainer.setDestinationName(USER_EVENT_TOPIC_DESTINATION);
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(userEventConsumer());
        messageListenerContainer.setMessageListener(messageListenerAdapter);
        messageListenerContainer.setSessionTransacted(true);
        messageListenerContainer.setTransactionManager(appContext.txManager());
        return messageListenerContainer;
    }
}
