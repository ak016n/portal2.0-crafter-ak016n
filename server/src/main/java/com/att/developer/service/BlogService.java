package com.att.developer.service;

import java.util.List;

import com.att.developer.bean.blog.BlogComment;

public interface BlogService {

	BlogComment createComment(String postId, String comment, String login);

	boolean createUser(String login);

	List<BlogComment> getComments(String postId);

	String getBlog(String postId);

}