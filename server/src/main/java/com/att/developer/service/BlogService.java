package com.att.developer.service;

import java.util.List;

import org.springframework.util.MultiValueMap;

import com.att.developer.bean.blog.BlogComment;
import com.att.developer.bean.blog.BlogPost;

public interface BlogService {

	BlogComment createComment(String postId, String comment, String login, String transactionId);

	List<BlogComment> getComments(String postId);

	BlogPost getBlog(String postId);

	List<BlogPost> getBlogs(MultiValueMap<String, String> allRequestParams);
	
	boolean createUser(String login, String transactionId);

}