package com.att.developer.bean.blog.builder;


import java.util.Date;

import com.att.developer.bean.blog.BlogPost;

public class BlogPostBuilder {

    public static final String HELLO_WORLD_POST = "hello world post";
	private BlogPost blogPost = new BlogPost();
    
    public BlogPostBuilder(){
        blogPost.setId(java.util.UUID.randomUUID().toString());
        blogPost.setParentId("0");
        blogPost.setAuthor(new BlogGetUserBuilder().build());
        blogPost.setContent(HELLO_WORLD_POST);
        blogPost.setDate(new Date());
        blogPost.setModified(new Date());
    }
    
    public BlogPost build(){
        return blogPost;
    }
    
}