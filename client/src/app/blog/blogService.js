angular.module('blog').factory('blogPostService', ['$resource', function($resource, $scope) {
		return $resource('/developer/comgw/posts/:postId', {}, {
			query: {isArray:true}
	    });
  }]);

angular.module('blog').factory('blogCommentService', ['$resource', function($resource, $scope) {
	return $resource('/developer/comgw/posts/:postId/comments/:commentId', {}, {
    });
}]);

angular.module('blog').factory('blogCategoriesService', ['$resource', function($resource, $scope) {
	return $resource('/developer/comgw/categories', {}, {
    });
}]);

angular.module('blog').factory('blogTagsService', ['$resource', function($resource, $scope) {
	return $resource('/developer/comgw/tags', {}, {
    });
}]);