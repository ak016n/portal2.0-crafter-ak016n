package com.att.developer.bean.blog.builder;


import com.att.developer.bean.User;
import com.att.developer.bean.blog.BlogUser;
import com.att.developer.bean.builder.UserBuilder;

public class BlogUserBuilder {

    private BlogUser blogUser;
    
    public BlogUserBuilder() {
    	blogUser = new BlogUser(new UserBuilder().build());
    }
    
    public BlogUserBuilder withUser(User user) {
    	blogUser = new BlogUser(user);
    	return this;
    }
    
    public BlogUser build() {
        return blogUser;
    }
    
}