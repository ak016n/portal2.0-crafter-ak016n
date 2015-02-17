package com.att.developer.bean.blog;

import java.util.Date;

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
	
	@JsonProperty("date")
	private Date dateCreated;

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

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public String toString() {
		return "BlogComment [id=" + id + ", content=" + content + ", postId="
				+ postId + ", parentId=" + parentId + ", dateCreated="
				+ dateCreated + "]";
	}

}
