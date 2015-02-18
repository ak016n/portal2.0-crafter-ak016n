package com.att.developer.service;

import com.att.developer.bean.blog.BlogComment;

public interface BlogService {

	BlogComment createComment(String postId, String comment, String login);

	boolean createUser(String login);

	String getComments(String postId);

	String getBlog(String postId);

}