package com.att.developer.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.att.developer.bean.blog.BlogComment;
import com.att.developer.bean.blog.BlogPost;
import com.att.developer.bean.blog.builder.BlogCommentBuilder;
import com.att.developer.bean.blog.builder.BlogPostBuilder;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.BlogService;
import com.att.developer.util.CookieUtil;

public class CommunityGatewayControllerTest {

    @Mock
    private CookieUtil mockCookieUtil;
    
    @Mock
    private BlogService mockBlogService;
	
    CommunityGatewayController communityGatewayController;
    
    @Before
    public void init() {
    	MockitoAnnotations.initMocks(this);
    	
    	communityGatewayController = new CommunityGatewayController();
    	
    	communityGatewayController.setBlogService(mockBlogService);
    	communityGatewayController.setCookieUtil(mockCookieUtil);
    }

    @Test (expected = ServerSideException.class)
    public void postComment_missingPostId() {
    	HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    	communityGatewayController.postComment(null, mockRequest, "comment");
    }
    
    @Test (expected = ServerSideException.class)
    public void postComment_missingComment() {
    	HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    	communityGatewayController.postComment("1", mockRequest, null);
    }
    
    @Test (expected = ServerSideException.class)
    public void postComment_missingAuthCookie() {
    	HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    	communityGatewayController.postComment("1", mockRequest, "comment");
    }
    
    @Test
    public void postComment_happyPath() {
    	HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    	Map<String, String> cookieMap = new HashMap<>();
    	cookieMap.put(CookieUtil.PORTAL_LOGIN, "raj_test");
    	
    	Mockito.when(mockBlogService.createComment(Mockito.eq("1"), Mockito.eq("comment"), Mockito.eq("raj_test"), Mockito.anyString())).thenReturn(new BlogCommentBuilder().build());
    	Mockito.when(mockCookieUtil.getPortalUserMap(Mockito.any(Cookie[].class))).thenReturn(cookieMap);
    	
    	BlogComment blogComment = communityGatewayController.postComment("1", mockRequest, "comment");
    	
    	Assert.assertNotNull(blogComment);
    	Assert.assertEquals("hello world comment", blogComment.getContent());
    }
    
    @Test (expected = ServerSideException.class)
    public void getComments_missingPostId() {
    	communityGatewayController.getComments(null);
    }
    
    @Test
    public void getComments_happyPath() {
    	List<BlogComment> listOfComments = new ArrayList<>();
    	listOfComments.add(new BlogCommentBuilder().build());
    	
    	Mockito.when(mockBlogService.getComments("1")).thenReturn(listOfComments);
    	
    	List<BlogComment> blogComments = communityGatewayController.getComments("1");
    	
    	Assert.assertNotNull(blogComments);
    	Assert.assertTrue(blogComments.size() == 1);
    }
    
    @Test (expected = ServerSideException.class)
    public void getBlog_missingPostId() {
    	communityGatewayController.getBlog(null);
    }
    
    @Test
    public void getBlog_happyPath() {
    	Mockito.when(mockBlogService.getBlog("1")).thenReturn(new BlogPostBuilder().build());
    	
    	BlogPost blogPost = communityGatewayController.getBlog("1");
    	
    	Assert.assertNotNull(blogPost);
    	Assert.assertEquals(BlogPostBuilder.HELLO_WORLD_POST, blogPost.getContent());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void getBlogsWithFilter_happyPath() {
    	List<BlogPost> blogPostColl = new ArrayList<>();
    	blogPostColl.add(new BlogPostBuilder().build());
    	
    	Mockito.when(mockBlogService.getBlogs(Mockito.any(MultiValueMap.class))).thenReturn(new ResponseEntity<List<BlogPost>>(blogPostColl, HttpStatus.OK));
    	
    	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    	params.put("X", new ArrayList<String>());
    	
    	ResponseEntity<List<BlogPost>> blogPosts = communityGatewayController.getBlogs(params);
    	
    	Assert.assertNotNull(blogPosts);
    	Assert.assertTrue(blogPosts.getBody().size() == 1);
    }
    
    @Test
    public void getUser_happyPath() {
    	
    	HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
    	
    	Map<String, String> cookieMap = new HashMap<>();
    	cookieMap.put(CookieUtil.PORTAL_LOGIN, "raj_test");
    	cookieMap.put(CookieUtil.PORTAL_USER, "raj");
    	
    	Mockito.when(mockRequest.getCookies()).thenReturn(new Cookie[] {new Cookie(CookieUtil.PORTAL_USER, "encrypted")});
    	Mockito.when(mockCookieUtil.getPortalUserMap(Mockito.any(Cookie[].class))).thenReturn(cookieMap);
    	
    	Map<String, String> userMap = communityGatewayController.getUser(mockRequest);
    	
    	Assert.assertNotNull(userMap);
    	Assert.assertEquals("raj_test", userMap.get(CommunityGatewayController.LOGIN));
    	Assert.assertEquals("raj", userMap.get(CommunityGatewayController.FIRST_NAME));
    }
}
