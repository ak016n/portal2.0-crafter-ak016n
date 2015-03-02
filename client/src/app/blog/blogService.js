angular.module('blog').factory('blogService', ['$resource', function($resource, $scope) {
	var view;
	var service = {};
	
	service.posts = function() {
		return $resource('/developer/comgw/posts/:postId', {}, {
			query: {isArray:true}
	    });
	};
	
	service.comments = function() {
		return $resource('/developer/comgw/posts/:postId/comments/:commentId', {}, {
			query: {isArray:true}
	    });
	};

	service.categories = function() {
		return $resource('/developer/comgw/categories', {}, {
	    });
	};
	
	service.tags = function() {
		return $resource('/developer/comgw/tags', {}, {
	    });
	};
	
	service.setView = function(view) {
		this.view = view;
	};
	
	service.getView = function() {
		return this.view;
	};
	
	return service;
}]);