package com.att.developer.service;

import com.att.developer.bean.blog.BlogComment;

public interface BlogService {

	BlogComment createComment(String postId, String comment, String login);

	boolean createUser(String login);

	//Its string instead of list of blog posts because we are using this a proxy
	String getComments(String postId);

}