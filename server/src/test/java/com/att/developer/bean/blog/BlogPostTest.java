package com.att.developer.bean.blog;

import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;

import com.att.developer.bean.blog.builder.BlogPostBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BlogPostTest {

	@Test
	public void convertObjToJson() throws JsonProcessingException {
		BlogPost blogPost = new BlogPostBuilder().build();
		
		ObjectMapper mapper = new ObjectMapper();
		
		String jsonOutput = mapper.writeValueAsString(blogPost);
		
		Assert.assertThat(jsonOutput, StringContains.containsString("\"author\":{\"username\":\"penny_test\",\"ID\":\"1\"}"));
		Assert.assertThat(jsonOutput, StringContains.containsString("\"content\":\"hello world post\",\"excerpt\":\"hello world\""));
		Assert.assertThat(jsonOutput, StringContains.containsString("\"date_modified\":\"2015-03-03 05:20:40\",\"date_created\":\"2015-03-03 05:20:40\""));
	}

}
