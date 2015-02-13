package com.att.developer.service;

import com.att.developer.bean.blog.BlogComment;

public interface BlogService {

	public abstract BlogComment createComment(String postId, String comment, String login);

	public abstract boolean createUser(String login);

}