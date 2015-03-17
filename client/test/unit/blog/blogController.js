describe('test BlogController', function() {
    //mock Application to allow us to inject our own dependencies
    beforeEach(angular.mock.module('portalApp'));
	
    var scope, sce, mockBlogService, state, mockFlashMessageService, q, httpBackend, deferred, callbackSuccess, callbackError, resource;
    
	beforeEach(angular.mock.inject(function($rootScope, $sce, $state, $controller, $q, $injector,  _$httpBackend_) {
		scope = $rootScope.$new();
		
		q = $q;
		
		$httpBackend = _$httpBackend_;
		
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
		
		controller = $controller;
		
		mockFlashMessageService = jasmine.createSpyObj('flashMessageService', ['setMessage']);
		spyOn(mockBlogService, 'posts').andCallThrough();
		spyOn(mockBlogService, 'categories').andCallThrough();
		
	}));
	
    it('should have called default "init"', function() {
    	
    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	expect(scope.pagination.totalItems).toBe(1);
    	expect(mockBlogService.posts).toHaveBeenCalled();
    });
    
    it('should have called "init blog.list"', function() {
    	
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
    
    it('should have called "init blog.entry"', function() {
    	
    	mockBlogService.setView('blog.entry');
    	response = {title : 'test', content: 'content desc 1'};

    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	
    	deferred.resolve(response);
    	scope.$digest();
    	
    	expect(scope.blog.post).toBe(response);
    	expect(mockBlogService.posts).toHaveBeenCalled();
    	expect(mockBlogService.categories).toHaveBeenCalled();
    });
    
    it('should have called "init blog.categories"', function() {
    	
    	mockBlogService.setView('blog.categories');
    	response = ['X', 'Y', 'Z'];

    	controller('BlogCtrl', {$scope: scope, blogService: mockBlogService, $sce: sce, $state: state, flashMessageService: mockFlashMessageService});
    	
    	deferred.resolve(response);
    	scope.$digest();
    	
    	expect(scope.blog.categories).toEqual([['X', 'Y'], ['Z']]); // slicing to fit the
    	expect(mockBlogService.categories).toHaveBeenCalled();
    });
	
});