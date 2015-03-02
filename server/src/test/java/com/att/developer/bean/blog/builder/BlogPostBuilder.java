package com.att.developer.bean.blog.builder;


import java.util.Date;

import com.att.developer.bean.blog.BlogPost;

public class BlogPostBuilder {

    private static final String HELLO_WORLD_EXCERPT = "hello world";
	public static final String HELLO_WORLD_POST = "hello world post";
	
	private BlogPost blogPost = new BlogPost();
    
    public BlogPostBuilder(){
        blogPost.setId(java.util.UUID.randomUUID().toString());
        blogPost.setParentId("0");
        blogPost.setAuthor(new BlogGetUserBuilder().build());
        blogPost.setContent(HELLO_WORLD_POST);
        blogPost.setExcerpt(HELLO_WORLD_EXCERPT);
        blogPost.setDateCreated(new Date(1425360040000L)); // corresponds to 2015-03-02 21:20:40
        blogPost.setModified(new Date(1425360040000L)); // corresponds to 2015-03-02 21:20:40
    }
    
    public BlogPost build(){
        return blogPost;
    }
    
}