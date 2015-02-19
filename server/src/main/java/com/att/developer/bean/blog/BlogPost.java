package com.att.developer.bean.blog;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlogPost {

	@JsonProperty("ID")
	private String id;
	
	private String title;
	
	private String content;
	
	@JsonProperty("parent")
	private String parentId;
	
	private Date date;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonProperty("date_created")
	private Date dateCreated;

	private Date modified;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonProperty("date_modified")
	private Date dateModified;
	
	private String slug;
	
	private BlogGetUser author;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Date getDateCreated() {
		return date;
	}

	public void setDate(Date dateCreated) {
		this.date = dateCreated;
	}

	public Date getDateModified() {
		return modified;
	}

	public void setModified(Date dateModified) {
		this.modified = dateModified;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public BlogGetUser getAuthor() {
		return author;
	}

	public void setAuthor(BlogGetUser author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "BlogPost [id=" + id + ", title=" + title + ", content="
				+ content + ", parentId=" + parentId + ", dateCreated="
				+ dateCreated + ", dateModified=" + dateModified + ", slug="
				+ slug + ", author=" + author + "]";
	}

}
