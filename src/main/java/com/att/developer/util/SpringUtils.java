package com.att.developer.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        CONTEXT = context;
    }

    public static ApplicationContext getApplicationContext() {
        return CONTEXT;
    }
    
    // Convenience method
    public static void setStaticApplicationContext(ApplicationContext context) {
        CONTEXT = context;
    }
    
    /**
     * Return Generic Object please cast it to appropriate type
     */
    public static Object getBean(String beanName) {
        return CONTEXT.getBean(beanName);
    }
}
