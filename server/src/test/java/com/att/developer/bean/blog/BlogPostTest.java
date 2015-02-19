package com.att.developer.bean.blog;

import org.junit.Test;

import com.att.developer.bean.blog.builder.BlogPostBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BlogPostTest {

	@Test
	public void convertObjToJson() throws JsonProcessingException {
		BlogPost blogPost = new BlogPostBuilder().build();
		
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writeValueAsString(blogPost));
		
	}

}
