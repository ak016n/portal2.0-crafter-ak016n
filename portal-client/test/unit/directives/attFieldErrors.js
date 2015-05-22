'use strict';

describe('attFieldErrors', function() {
	var scope, compiled, elem, html, $httpBackend;//we'll use this scope in our tests
	
	 beforeEach(angular.mock.module('portalApp'));
	 
	 beforeEach(angular.mock.inject(function($rootScope, $compile, _$httpBackend_) {
	        scope = $rootScope.$new();
	        $httpBackend = _$httpBackend_;
	        $httpBackend.whenGET('/developer/i18N?lang=en').respond({});	        
	        
	        html = '<form name="form" att-form-errors><div><label>Field Label</label><input type="text" name="fieldName" required ng-model="fieldName" att-field-errors/></div></form>';
	        
	        elem = angular.element(html);
	        
	        compiled = $compile(elem);
	        
	        compiled(scope);
	        
	        scope.$digest();
	    	// This is for translate
	    	
	    }));
	 
	 it("should set error when empty and error removed when not empty", function() {
		 scope.fieldName = '';
		 expect(elem.children().find('label').next().text()).toEqual('fieldName.required');
		 scope.fieldName = 'a';
		 scope.$digest();
		 expect(elem.children().find('label').next().text()).not.toEqual('fieldName.required');
	 });
});