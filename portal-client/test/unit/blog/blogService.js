'use strict';

describe('test blogService', function() {
	var scope, $httpBackend, blogService;//we'll use this scope in our tests
	
	 beforeEach(angular.mock.module('blog'));
	 
	 beforeEach(angular.mock.inject(function($rootScope, _$httpBackend_,  $injector) {
	    	$httpBackend = _$httpBackend_;
	        scope = $rootScope.$new();
	       // blogService = $injector.get('blogService');
	 }));
	 
	 it("should return single blog post on success", inject(['blogService', function(blogService) {
		 $httpBackend.expectGET('/developer/comgw/posts/x').respond({slug: 'x'});
		 var response = blogService.posts().get({postId: 'x'});
		 $httpBackend.flush();
		 expect(response.slug).toEqual('x');
	 }]));
	 
	 it("should return blog posts on success", inject(['blogService', function(blogService) {
		 $httpBackend.expectGET('/developer/comgw/posts').respond([{slug: '1'}, {slug: '2'}]);
		 var response = blogService.posts().query({});
		 $httpBackend.flush();
		 expect(response[0].slug).toEqual('1');
		 expect(response[1].slug).toEqual('2');
	 }]));
	 
	 it("should return comments for blog post x on success", inject(['blogService', function(blogService) {
		 $httpBackend.expectGET('/developer/comgw/posts/x/comments').respond([{comment_id: '1'}, {comment_id: '2'}]);
		 var response = blogService.comments().query({postId : 'x'});
		 $httpBackend.flush();
		 expect(response[0].comment_id).toEqual('1');
		 expect(response[1].comment_id).toEqual('2');
	 }]));
	 
	 it("should return categories on success", inject(['blogService', function(blogService) {
		 $httpBackend.expectGET('/developer/comgw/categories').respond([{categories_id: '1'}, {categories_id: '2'}]);
		 var response = blogService.categories().query({});
		 $httpBackend.flush();
		 expect(response[0].categories_id).toEqual('1');
		 expect(response[1].categories_id).toEqual('2');
	 }]));
	 
	 it("should return categories on success", inject(['blogService', function(blogService) {
		 $httpBackend.expectGET('/developer/comgw/tags').respond([{tags_id: '1'}, {tags_id: '2'}]);
		 var response = blogService.tags().query({});
		 $httpBackend.flush();
		 expect(response[0].tags_id).toEqual('1');
		 expect(response[1].tags_id).toEqual('2');
	 }]));
	 
	 it("should return view 'x' when after setting 'x' on success", inject(['blogService', function(blogService) {
		 var response = blogService.setView('x');
		 expect(blogService.getView()).toEqual('x');
	 }]));
	 
});