package com.att.developer.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import com.att.developer.bean.blog.BlogComment;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;

public class BlogServiceImplTest {

	BlogServiceImpl blogService;
	
    @Mock
    private GlobalScopedParamService mockGlobalScopedParamService;
    
    @Mock
    private UserProfileService mockUserProfileService;
    
    MockRestServiceServer mockServer;
    
    @Before
    public void init() {
    	MockitoAnnotations.initMocks(this);
    	blogService = new BlogServiceImpl();
    	
    	
    	blogService.setGlobalScopedParamService(mockGlobalScopedParamService);
    	blogService.setUserProfileService(mockUserProfileService);
    	
    	Mockito.when(mockGlobalScopedParamService.get("blog_host", "http://141.204.193.91/wp-json/")).thenReturn("http://dummyHost/");
    	Mockito.when(mockGlobalScopedParamService.get("blog_comment_status", "approved")).thenReturn("approved");
    	Mockito.when(mockGlobalScopedParamService.get("blog_admin_permission", "YWRtaW46cGFzc3dvcmQxMjM=")).thenReturn("xyz");
    	
   	 	RestTemplate restTemplate = new RestTemplate();
   	 	mockServer = MockRestServiceServer.createServer(restTemplate);
   	 
   	 	blogService.setRestTemplate(restTemplate);
    	blogService.init();
    }
    
    @Test
    public void createComment_happyPath() {
    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/users/raj_test"))
    											.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
    											.andRespond(MockRestResponseCreators.withSuccess("{\"id\" : \"42\"}", MediaType.APPLICATION_JSON));

    	mockServer.expect(MockRestRequestMatchers.requestTo("http://dummyHost/posts/1/comments"))
    											.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
    											.andRespond(MockRestResponseCreators.withSuccess("{\"ID\":35,\"post\":1,\"content\":\"<p>rebuilding postman<\\/p>\",\"status\":\"approved\",\"type\":\"comment\",\"parent\":0,\"author\":{\"ID\":7,\"username\":\"regular\"},\"date\":\"2015-02-17T01:00:04+00:00\",\"date_tz\":\"UTC\",\"date_gmt\":\"2015-02-17T01:00:04+00:00\"}", MediaType.APPLICATION_JSON));

    	
    	BlogComment blogComment = blogService.createComment("1", "comment", "raj_test");
    	
    	Assert.assertNotNull(blogComment);
    	Assert.assertEquals("35", blogComment.getId());
    	mockServer.verify();
    }
}
