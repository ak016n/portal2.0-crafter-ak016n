package com.att.developer.bean.blog.builder;


import com.att.developer.bean.User;
import com.att.developer.bean.blog.BlogCreateUser;
import com.att.developer.bean.builder.UserBuilder;

public class BlogUserBuilder {

    private BlogCreateUser blogUser;
    
    public BlogUserBuilder() {
    	blogUser = new BlogCreateUser(new UserBuilder().build());
    }
    
    public BlogUserBuilder withUser(User user) {
    	blogUser = new BlogCreateUser(user);
    	return this;
    }
    
    public BlogCreateUser build() {
        return blogUser;
    }
    
}