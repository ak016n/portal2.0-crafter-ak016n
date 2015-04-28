package com.att.developer.bean.builder;


import java.time.Instant;

import com.att.developer.bean.ApiBundle;


public class ApiBundleBuilder {

    private ApiBundle apiBundle = new ApiBundle();
    
    public ApiBundleBuilder(){
    	Instant now = Instant.now();
        apiBundle.setStartDate(now);
        apiBundle.setEndDate(now.plusSeconds(15000));
        apiBundle.setName("n_" + apiBundle.getId());
        apiBundle.setComments("some comments");
        apiBundle.setCreatedOn(now);
        apiBundle.setLastUpdated(now);
    }
    
    public ApiBundle build(){
        return apiBundle;
    }
    
    public ApiBundleBuilder withId(String id){
        this.apiBundle.setId(id);
        return this;
    }
    
    public ApiBundleBuilder withName(String name){
        this.apiBundle.setName(name);
        return this;
    }
    
}