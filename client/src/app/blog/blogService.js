angular.module('blog').factory('blogPostService', ['$resource', function($resource, $scope) {
		return $resource('/developer/comgw/posts/:postId', {}, {
			query: {isArray:true}
	    });
  }]);

angular.module('blog').factory('blogCommentService', ['$resource', function($resource, $scope) {
	return $resource('/developer/comgw/posts/:postId/comments/:commentId', {}, {
    });
}]);