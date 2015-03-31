package com.att.developer.bean.blog;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlogComment {

	@JsonProperty("ID")
	private String id;
	
	private String content;
	
	@JsonProperty("post")
	private String postId;
	
	@JsonProperty("parent")
	private String parentId;
	
	private Date dateCreated;

	private BlogGetUser author;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonProperty("date_created")
	public Date getDateCreated() {
		return dateCreated;
	}

	@JsonProperty("date")
	public void setDate(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public BlogGetUser getAuthor() {
		return author;
	}

	public void setAuthor(BlogGetUser author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "BlogComment [id=" + id + ", content=" + content + ", postId="
				+ postId + ", parentId=" + parentId + ", dateCreated="
				+ dateCreated + ", author=" + author + "]";
	}

}
