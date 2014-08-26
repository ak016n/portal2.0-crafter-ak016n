'use strict';
 
describe('AdminController', function(){
    var scope, mockAdminService, mockAdminVersionService, mockGlobalHandleErrorService, mockForm, q, deferred, mockTranslateProvider, $httpBackend;//we'll use this scope in our tests
 
    //mock Application to allow us to inject our own dependencies
    beforeEach(angular.mock.module('portalApp'));
    //mock the controller for the same reason and include $rootScope and $controller
    beforeEach(angular.mock.inject(function($rootScope, $controller, $q, _$httpBackend_) {
        //create an empty scope
    	q = $q;
    	
    	mockForm = {
    			$setPristine: function() {
    			}
    	};
    	
    	$httpBackend = _$httpBackend_;
    	
        scope = $rootScope.$new();
        
    	mockAdminService = {
    			get: function () {
    				deferred = q.defer();
    				return {$promise: deferred.promise};
    			}
    	};
    	
    	mockAdminVersionService = jasmine.createSpyObj('mockAdminVersionService', ['get']);
    	mockAdminVersionService.get.andCallFake(function(x){
    		deferred = q.defer();
			return {$promise: deferred.promise};
    	});
    	
    	// This is for translate
    	$httpBackend.when('GET', '/developer/i18N?lang=en').respond({});

    	spyOn(mockAdminService, 'get').andCallThrough();
    	
    	mockGlobalHandleErrorService = jasmine.createSpy('globalHandleErrorService');
    	
    	mockGlobalHandleErrorService.clearAllErrors = jasmine.createSpy('globalHandleErrorService');
    	
        //declare the controller and inject our empty scope
        $controller('AdminController', {$scope: scope, adminService: mockAdminService, adminVersionService: mockAdminVersionService, globalHandleErrorService: mockGlobalHandleErrorService});
    }));

    it('should have been "empty"', function(){
    	expect(scope.adminProp.description).toBe('empty');
    });
    
    it('should have returned a=b', function(){
    	// mock response converted from json to obj
    	var mockResponse = {
    			  itemKey: 'a',
    			  fieldKey: 'b',
    			  description: 'a=b'
    	};
    	
    	mockForm.$invalid = false;
    	scope.refresh(mockForm);
    	deferred.resolve(mockResponse);
    	scope.$digest();
        expect(scope.adminProp.description).toBe('a=b');
        expect(mockAdminVersionService.get).toHaveBeenCalled();
    });
    
    it('should have called globalHandleErrorService', function(){
    	var error = {
    			data: {
    				errors: {
    					x: 'y'
    				}
    			}
    	};
    	// mock response converted from json to obj
    	mockForm.$invalid = false;
    	scope.refresh(mockForm);
    	deferred.reject(error);
    	scope.$digest();
        expect(scope.adminProp.description).toBe('');
        expect(mockGlobalHandleErrorService).toHaveBeenCalled();
    });    

});