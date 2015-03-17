'use strict';

describe('test flashMessageService', function() {
	var scope, $httpBackend, flashMessageService;//we'll use this scope in our tests
	
	beforeEach(angular.mock.module('portalApp'));
	 
	 it("should return response on success", inject(['flashMessageService', function(flashMessageService) {
		 flashMessageService.setMessage([{message: 'x'}, {message: 'y'}]);
		 expect(flashMessageService.getMessage()).toEqual(['x', 'y']);
	 }]));
	 
	 
	 it("should return response eliminating duplicates on success", inject(['flashMessageService', function(flashMessageService) {
		 flashMessageService.setMessage([{message: 'x'}, {message: 'x'}]);
		 expect(flashMessageService.getMessage()).toEqual(['x']);
	 }]));
});
