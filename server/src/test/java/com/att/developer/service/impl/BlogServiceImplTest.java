package com.att.developer.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.att.developer.bean.EventLog;
import com.att.developer.bean.blog.BlogComment;
import com.att.developer.bean.blog.BlogPost;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.EventTrackingService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;

public class BlogServiceImplTest {

	BlogServiceImpl blogService;
	
    @Mock
    private GlobalScopedParamService mockGlobalScopedParamService;
    
    @Mock
    private UserProfileService mockUserProfileService;
    
    @Mock
    private EventTrackingService mockEventTrackingService;
    
    MockRestServiceServer mockServer;
    
    @Before
    public void init() {
    	MockitoAnnotations.initMocks(this);
    	blogService = new BlogServiceImpl();
    	
    	
    	blogService.setGlobalScopedParamService(mockGlobalScopedParamService);
    	blogService.setUserProfileService(mockUserProfileService);
    	blogService.setEventTrackingService(mockEventTrackingService);
    	
    	Mockito.when(mockGlobalScopedParamService.get(BlogServiceImpl.BLOG_HOST_KEY, BlogServiceImpl.DEFAULT_BLOG_HOST)).thenReturn("http://dummyHost/");
    	
   	 	RestTemplate restTemplate = new RestTemplate();
   	 	mockServer = MockRestServiceServer.createServer(restTemplate);
   	 
   	 	blogService.setRestTemplate(restTemplate);
    }
    
    @Test
    public void createComment_happyPathExistingUser() {
    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/users/raj_test"))
    				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    				.andRespond(MockRestResponseCreators.withSuccess("{\"id\" : \"42\"}", MediaType.APPLICATION_JSON));

    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/1/comments"))
    				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
    				.andRespond(MockRestResponseCreators.withSuccess("{\"ID\":35,\"post\":1,\"content\":\"<p>rebuilding postman<\\/p>\",\"status\":\"approved\",\"type\":\"comment\",\"parent\":0,\"author\":{\"ID\":7,\"username\":\"regular\"},\"date\":\"2015-02-17T01:00:04+00:00\",\"date_tz\":\"UTC\",\"date_gmt\":\"2015-02-17T01:00:04+00:00\"}", MediaType.APPLICATION_JSON));

    	BlogComment blogComment = blogService.createComment("1", "comment", "raj_test", java.util.UUID.randomUUID().toString());
    	
    	Mockito.verify(mockEventTrackingService, Mockito.atLeastOnce()).writeEvent(Mockito.any(EventLog.class));
    	Assert.assertNotNull(blogComment);
    	Assert.assertEquals("35", blogComment.getId());
    	mockServer.verify();
    }
    
    @Test
    public void createComment_happyPathNonExistingUser() {
    	
    	Mockito.when(mockUserProfileService.getUser("raj_test")).thenReturn(new UserBuilder().build());
    	
    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/users/raj_test"))
    				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST).body("[{\"code\":\"json_user_invalid_username\",\"message\":\"Invalid user name.\"}]"));

    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/users/"))
					.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
					.andRespond(MockRestResponseCreators.withSuccess("{\"ID\":7,\"username\":\"regular\"}", MediaType.APPLICATION_JSON));
    	
    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/1/comments"))
    				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
    				.andRespond(MockRestResponseCreators.withSuccess("{\"ID\":35,\"post\":1,\"content\":\"<p>rebuilding postman<\\/p>\",\"status\":\"approved\",\"type\":\"comment\",\"parent\":0,\"author\":{\"ID\":7,\"username\":\"regular\"},\"date\":\"2015-02-17T01:00:04+00:00\",\"date_tz\":\"UTC\",\"date_gmt\":\"2015-02-17T01:00:04+00:00\"}", MediaType.APPLICATION_JSON));

    	BlogComment blogComment = blogService.createComment("1", "comment", "raj_test", java.util.UUID.randomUUID().toString());
    	
    	Mockito.verify(mockEventTrackingService, Mockito.times(2)).writeEvent(Mockito.any(EventLog.class));
    	Assert.assertNotNull(blogComment);
    	Assert.assertEquals("35", blogComment.getId());
    	mockServer.verify();
    }
    
    @Test
    public void createComment_alreadyExistingEmailException() {
    	
    	Mockito.when(mockUserProfileService.getUser("raj_test")).thenReturn(new UserBuilder().build());
    	
    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/users/raj_test"))
    				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST).body("[{\"code\":\"json_user_invalid_username\",\"message\":\"Invalid user name.\"}]"));

    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/users/"))
					.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
					.andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST).body("[{\"code\":\"json_user_email_already_exists\",\"message\":\"Email Taken.\"}]"));
    	
    	try {
    		blogService.createComment("1", "comment", "raj_test", java.util.UUID.randomUUID().toString());
    		Assert.fail();
    	} catch (ServerSideException e) {
    		Assert.assertEquals("json_user_email_already_exists", e.getServerSideErrors().getErrorColl().get(0).getId());
    	}
    	
    	mockServer.verify();
    }
    
    @Test
    public void createComment_internalServerErrorFetchingUser() {
    	
    	Mockito.when(mockUserProfileService.getUser("raj_test")).thenReturn(new UserBuilder().build());
    	
    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/users/raj_test"))
    				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    				.andRespond(MockRestResponseCreators.withBadRequest());

    	try {
    		blogService.createComment("1", "comment", "raj_test", java.util.UUID.randomUUID().toString());
    		Assert.fail();
    	} catch (RuntimeException e) {
    		Assert.assertEquals("org.springframework.web.client.HttpClientErrorException: 400 Bad Request", e.getMessage());
    	}
    	
    	mockServer.verify();
    }
    
    @Test
    public void createComment_errorCreatingPost() {
    	
       	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/users/raj_test"))
		.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
		.andRespond(MockRestResponseCreators.withSuccess("{\"id\" : \"42\"}", MediaType.APPLICATION_JSON));

		mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/1/comments"))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST).body("[{\"code\":\"json_missing_callback_param\",\"message\":\"Missing parameter data\"}]"));

    	try {
    		blogService.createComment("1", "comment", "raj_test", java.util.UUID.randomUUID().toString());
    		Assert.fail();
    	} catch (ServerSideException e) {
    		Assert.assertEquals("json_missing_callback_param", e.getServerSideErrors().getErrorColl().get(0).getId());
    	}
    	
    	mockServer.verify();
    }
    
    @Test
    public void getComment_happyPath() {
    	
       	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/1/comments"))
		.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
		.andRespond(MockRestResponseCreators.withSuccess("[{\"ID\":35,\"post\":1,\"content\":\"<p>comment1</p>\",\"status\":\"approved\",\"type\":\"comment\",\"parent\":0,\"author\":{\"ID\":7,\"username\":\"regular\"},\"date\":\"2015-02-17T01:00:04+00:00\",\"date_tz\":\"UTC\",\"date_gmt\":\"2015-02-17T01:00:04+00:00\"},"
				+ "{\"ID\":36,\"post\":1,\"content\":\"<p>comment2</p>\",\"status\":\"approved\",\"type\":\"comment\",\"parent\":0,\"author\":{\"ID\":7,\"username\":\"regular\"},\"date\":\"2015-02-17T01:00:04+00:00\",\"date_tz\":\"UTC\",\"date_gmt\":\"2015-02-17T01:00:04+00:00\"}]", MediaType.APPLICATION_JSON));

       	List<BlogComment> listOfComments = blogService.getComments("1");

       	Assert.assertNotNull(listOfComments);
       	Assert.assertTrue(listOfComments.size() == 2);
    	mockServer.verify();
    }
    
    @Test
    public void getComment_errorHandling() {
    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/1/comments"))
    				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    				.andRespond(MockRestResponseCreators.withBadRequest());

    	try {
    		blogService.getComments("1");
    		Assert.fail();
    	} catch (RuntimeException e) {
    		Assert.assertEquals("org.springframework.web.client.HttpClientErrorException: 400 Bad Request", e.getMessage());
    	}
    	
    	mockServer.verify();
    }
    
    @Test
    public void getBlog_happyPath() {
    	
       	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/1"))
		.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
		.andRespond(MockRestResponseCreators.withSuccess("{\"title\":\"Post 2\",\"status\":\"publish\",\"type\":\"post\",\"content\":\"<p>First post using rest api</p>\\n\",\"parent\":0,\"comment_status\":\"open\"}", MediaType.APPLICATION_JSON));

       	BlogPost blog = blogService.getBlog("1");

       	Assert.assertNotNull(blog);
       	Assert.assertEquals("Post 2", blog.getTitle());
    	mockServer.verify();
    }
    
    @Test
    public void getBlog_errorHandling() {
    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/1"))
    				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    				.andRespond(MockRestResponseCreators.withBadRequest());

    	try {
    		blogService.getBlog("1");
    		Assert.fail();
    	} catch (RuntimeException e) {
    		Assert.assertEquals("org.springframework.web.client.HttpClientErrorException: 400 Bad Request", e.getMessage());
    	}
    	
    	mockServer.verify();
    }
    
    @Test
    public void getBlogs_happyPath() {
       	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/?filter[posts_per_page]=2"))
		.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
		.andRespond(MockRestResponseCreators.withSuccess("[{\"title\":\"New Post for testing\",\"content\":\"\",\"slug\":\"new-post-for-testing\",\"author\":{\"username\":\"raj_test\",\"ID\":\"2\"},\"ID\":\"11\",\"parent\":\"0\",\"date_created\":\"2015-02-17 12:45:16\",\"date_modified\":\"2015-02-17 12:45:16\"},"
				+ "{\"title\":\"test post\",\"content\":\"<p>test post</p>\\n<p>&nbsp;</p>\\n<p>&nbsp;</p>\\n<p>&nbsp;</p>\\n\",\"slug\":\"test-post\",\"author\":{\"username\":\"raj_test\",\"ID\":\"2\"},\"ID\":\"6\",\"parent\":\"0\",\"date_created\":\"2015-02-17 12:00:30\",\"date_modified\":\"2015-02-17 12:00:30\"}]", MediaType.APPLICATION_JSON));

    	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    	List<String> valueColl = new ArrayList<>();
    	valueColl.add("2");
    	params.put("filter[posts_per_page]", valueColl);
       	
       	ResponseEntity<List<BlogPost>> blogs = blogService.getBlogs(params);

       	Assert.assertNotNull(blogs);
       	Assert.assertTrue(blogs.getBody().size() == 2);
    	mockServer.verify();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void getBlogs_missingParam() {
       	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/"))
		.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
		.andRespond(MockRestResponseCreators.withSuccess("[{\"title\":\"New Post for testing\",\"content\":\"\",\"slug\":\"new-post-for-testing\",\"author\":{\"username\":\"raj_test\",\"ID\":\"2\"},\"ID\":\"11\",\"parent\":\"0\",\"date_created\":\"2015-02-17 12:45:16\",\"date_modified\":\"2015-02-17 12:45:16\"},"
				+ "{\"title\":\"test post\",\"content\":\"<p>test post</p>\\n<p>&nbsp;</p>\\n<p>&nbsp;</p>\\n<p>&nbsp;</p>\\n\",\"slug\":\"test-post\",\"author\":{\"username\":\"raj_test\",\"ID\":\"2\"},\"ID\":\"6\",\"parent\":\"0\",\"date_created\":\"2015-02-17 12:00:30\",\"date_modified\":\"2015-02-17 12:00:30\"}]", MediaType.APPLICATION_JSON));

       	blogService.getBlogs(null);
    	mockServer.verify();
    }
    
    @Test
    public void getBlogs_emptyParam() {
       	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/"))
		.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
		.andRespond(MockRestResponseCreators.withSuccess("[{\"title\":\"New Post for testing\",\"content\":\"\",\"slug\":\"new-post-for-testing\",\"author\":{\"username\":\"raj_test\",\"ID\":\"2\"},\"ID\":\"11\",\"parent\":\"0\",\"date_created\":\"2015-02-17 12:45:16\",\"date_modified\":\"2015-02-17 12:45:16\"},"
				+ "{\"title\":\"test post\",\"content\":\"<p>test post</p>\\n<p>&nbsp;</p>\\n<p>&nbsp;</p>\\n<p>&nbsp;</p>\\n\",\"slug\":\"test-post\",\"author\":{\"username\":\"raj_test\",\"ID\":\"2\"},\"ID\":\"6\",\"parent\":\"0\",\"date_created\":\"2015-02-17 12:00:30\",\"date_modified\":\"2015-02-17 12:00:30\"}]", MediaType.APPLICATION_JSON));

    	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
       	
       	ResponseEntity<List<BlogPost>> blogs = blogService.getBlogs(params);

       	Assert.assertNotNull(blogs);
       	Assert.assertTrue(blogs.getBody().size() == 2);
    	mockServer.verify();
    }
    
    @Test
    public void getBlogs_errorHandling() {
    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/"))
    				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    				.andRespond(MockRestResponseCreators.withBadRequest());

    	try {
        	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    		
    		blogService.getBlogs(params);
    		Assert.fail();
    	} catch (RuntimeException e) {
    		Assert.assertEquals("org.springframework.web.client.HttpClientErrorException: 400 Bad Request", e.getMessage());
    	}
    	
    	mockServer.verify();
    }
}
