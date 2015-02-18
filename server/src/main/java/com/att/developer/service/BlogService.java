package com.att.developer.service;

import java.util.List;

import com.att.developer.bean.blog.BlogComment;
import com.att.developer.bean.blog.BlogPost;

public interface BlogService {

	BlogComment createComment(String postId, String comment, String login);

	boolean createUser(String login);

	List<BlogComment> getComments(String postId);

	BlogPost getBlog(String postId);

}