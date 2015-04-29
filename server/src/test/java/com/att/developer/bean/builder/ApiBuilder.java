package com.att.developer.bean.builder;

import com.att.developer.bean.api.Api;


public class ApiBuilder {

    private Api api = new Api();
    
    public ApiBuilder(){
    	api.setId(java.util.UUID.randomUUID().toString());
    	api.setName("SMS");
    }
    
    public Api build(){
        return api;
    }
    
    public ApiBuilder withId(String id){
        this.api.setId(id);
        return this;
    }
    
    public ApiBuilder withName(String name){
        this.api.setName(name);
        return this;
    }
    
}