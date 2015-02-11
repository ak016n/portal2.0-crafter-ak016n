package com.att.developer.service;

public interface BlogService {

	public abstract void createComment(String postId, String comment,
			String login);

	public abstract boolean createUser(String login);

}