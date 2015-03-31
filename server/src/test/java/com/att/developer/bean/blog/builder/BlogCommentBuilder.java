package com.att.developer.bean.blog.builder;


import java.util.Date;

import com.att.developer.bean.blog.BlogComment;

public class BlogCommentBuilder {

    private BlogComment blogComment = new BlogComment();
    
    public BlogCommentBuilder(){
        blogComment.setId(java.util.UUID.randomUUID().toString());
        blogComment.setPostId("1");
        blogComment.setParentId("0");
        blogComment.setContent("hello world comment");
        blogComment.setDate(new Date());
    }
    
    public BlogComment build(){
        return blogComment;
    }
    
}