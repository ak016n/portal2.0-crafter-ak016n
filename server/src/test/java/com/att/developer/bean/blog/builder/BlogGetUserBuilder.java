package com.att.developer.bean.blog.builder;


import com.att.developer.bean.blog.BlogGetUser;

public class BlogGetUserBuilder {

    private BlogGetUser blogUser;
    
    public BlogGetUserBuilder() {
    	blogUser = new BlogGetUser();
    	blogUser.setId("1");
    	blogUser.setUsername("penny_test");
    }
    
    public BlogGetUser build() {
        return blogUser;
    }
    
}