'use strict';

describe('adminService', function() {
	var scope, $httpBackend;//we'll use this scope in our tests
	
	 beforeEach(angular.mock.module('admin'));
	 beforeEach(angular.mock.inject(function($rootScope, _$httpBackend_) {
	    	$httpBackend = _$httpBackend_;
	        scope = $rootScope.$new();
	    	// This is for translate
	    	$httpBackend.expectGET('/developer/admin/x/y').respond({description: 'not empty'});
	    }));
	 
	 it("should return response on success", inject(['adminService', function(adminService) {
		 var response = adminService.get({itemKey: 'x', fieldKey: 'y'});
		 $httpBackend.flush();
		 expect(response.description).toEqual('not empty');
	 }]));
});
