package com.att.developer.bean.blog;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BlogPost {

	@JsonProperty("ID")
	private String id;
	
	private String title;
	
	private String content;
	
	@JsonProperty("parent")
	private String parentId;
	
	private Date dateCreated;

	private Date modified;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonProperty("date_modified")
	private Date dateModified;
	
	private String slug;
	
	private BlogGetUser author;
	
	@JsonProperty("image_url")
	private String image_url;
	
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

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonProperty("date_created")
	public Date getDateCreated() {
		return dateCreated;
	}

	@JsonProperty("date")
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
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
	
	public String getImage_url() {
		String tempUrl = null;
		if(StringUtils.isNotBlank(image_url)) {
			tempUrl = "/wp-content/uploads/" + image_url;
		}
		return tempUrl;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	
	@Override
	public String toString() {
		return "BlogPost [id=" + id + ", title=" + title + ", content="
				+ content + ", parentId=" + parentId
				+ ", dateCreated=" + dateCreated + ", modified=" + modified
				+ ", dateModified=" + dateModified + ", slug=" + slug
				+ ", author=" + author + ", image_url=" + image_url + "]";
	}

}
