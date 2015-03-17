describe('test BlogController', function() {
    //mock Application to allow us to inject our own dependencies
    beforeEach(angular.mock.module('portalApp'));
	
    var scope, sce, mockBlogService, state, mockFlashMessageService, q, $httpBackend, deferred, callbackSuccess, callbackError, resource;
    
	beforeEach(angular.mock.inject(function($rootScope, $sce, $state, $controller, $q, $injector,  _$httpBackend_) {
		scope = $rootScope.$new();
		
		q = $q;
		$httpBackend = _$httpBackend_;
		controller = $controller;
		
    	// This is for translate
    	$httpBackend.when('GET', '/developer/i18N?lang=en').respond({});
		
		// Mock State Params
		state = {
				params :  {
						s : '',
						id: 'x'
					}
		};
		
		//Mock SCE security - pass through
		sce = {
				trustAsHtml : function(content) {
					return content;
				}
		}
		
		//Mock blog service
		mockBlogService = {
			categories : function() {
			   return {
				   query: function() {
					   deferred = $q.defer();
					   return {$promise: deferred.promise};
			      }
			   }
			},
			posts : function() {
				   return {
					   query: function(param, callbackS, callbackE) {
						   callbackSuccess = callbackS;
						   callbackError = callbackE;
						   return;
				      },
					   get: function() {
						   deferred = $q.defer();
						   return {$promise: deferred.promise};
				      }
				   }
			},
			comments : function() {
				return {
					query: function() {
							deferred = $q.defer();
						   return {$promise: deferred.promise};
				    },
					save: function() {
						deferred = $q.defer();
					   return {$promise: deferred.promise};
					}
				}
			}
		};
		
		var view;
		
		mockBlogService.setView = function(view) {
			this.view = view;
		};
		
		mockBlogService.getView = function() {
			return this.view;
		};
		
		mockFlashMessageService = jasmine.createSpyObj('flashMessageService', ['setMessage', 'getMessage', 'addMessage', 'setError', 'isError', 'clearAll']);
		spyOn(mockBlogService, 'posts').andCallThrough();
		spyOn(mockBlogService, 'categories').andCallThrough();
		spyOn(mockBlogService, 'comments').andCallThrough();
		
	}));
	
    it('should have called default "init blog.list" for default', function() {
    	
    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	expect(scope.pagination.totalItems).toBe(1);
    	expect(mockBlogService.posts).toHaveBeenCalled();
    });
    
    it('should have called "init blog.list"  when callback success', function() {
    	
    	mockBlogService.setView('blog.list');
    	response = [{title : 'test', content: 'content desc 1'}];

    	header = function(string) {
    			return "10";
    	}
    	
    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	
    	callbackSuccess(response, header);
    	
    	expect(scope.blog.posts).toBe(response);
    	expect(mockBlogService.posts).toHaveBeenCalled();
    });
    
    it('should throw error "init blog.list" when callback error', function() {
    	
    	mockBlogService.setView('blog.list');
    	errorResponse = {
    			data : {"errors":[{"id":"Unexpected","message":"error message"}]}
    	};

    	header = function(string) {
    			return "10";
    	}
    	
    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	
    	callbackError(errorResponse);
    	
    	expect(mockFlashMessageService.setError).toHaveBeenCalled();
    	expect(mockFlashMessageService.setMessage).toHaveBeenCalled();
    	expect(mockBlogService.posts).toHaveBeenCalled();
    });
    
    it('should have called "init blog.entry" for blog.entry view', function() {
    	
    	mockBlogService.setView('blog.entry');
    	response = {title : 'test', content: 'content desc 1'};

    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	
    	deferred.resolve(response);
    	scope.$digest();
    	
    	expect(scope.blog.post).toBe(response);
    	expect(mockBlogService.posts).toHaveBeenCalled();
    	expect(mockBlogService.categories).toHaveBeenCalled();
    });
    
    it('should have called "init blog.categories" for blog.categories view', function() {
    	
    	mockBlogService.setView('blog.categories');
    	response = ['X', 'Y', 'Z'];

    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	
    	deferred.resolve(response);
    	scope.$digest();
    	
    	expect(scope.blog.categories).toEqual([['X', 'Y'], ['Z']]); // slicing to fit the
    	expect(mockBlogService.categories).toHaveBeenCalled();
    });
    
    it('should have thrown error "init blog.categories"', function() {
    	
    	mockBlogService.setView('blog.categories');
    	errorResponse = {
    			data : {"errors":[{"id":"Unexpected","message":"error message"}]}
    	};

    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	
    	deferred.reject(errorResponse);
    	scope.$digest();
    	
    	expect(mockFlashMessageService.setError).not.toHaveBeenCalled();
    	expect(mockFlashMessageService.setMessage).toHaveBeenCalled();
    	expect(mockBlogService.categories).toHaveBeenCalled();
    });
	
    it('should have post comment successfully', function() {
    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	
    	scope.blog.comment = {
    			content : 'raj vs howard'
    	};
    	scope.postComment('1', '1');
    	
    	deferred.resolve();
    	scope.$digest();
    	
    	expect(mockFlashMessageService.addMessage).toHaveBeenCalled();
    	expect(mockBlogService.comments).toHaveBeenCalled();
    });
    
    it('should have post comment failure', function() {
    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	
    	scope.blog.comment = {
    			content : 'raj vs howard'
    	};
    	scope.postComment('1', '1');
    	errorResponse = {
    			data : {"errors":[{"id":"Unexpected","message":"error message"}]}
    	};
    	
    	deferred.reject(errorResponse);
    	scope.$digest();
    	
    	expect(mockFlashMessageService.addMessage).not.toHaveBeenCalled();
    	expect(mockFlashMessageService.setMessage).toHaveBeenCalled();
    	expect(mockBlogService.comments).toHaveBeenCalled();
    });
	
});