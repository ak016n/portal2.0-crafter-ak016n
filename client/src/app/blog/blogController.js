angular.module('blog').controller('BlogCtrl', BlogCtrl);

BlogCtrl.$inject = ['$scope', '$sce', 'blogPostService'];

function BlogCtrl($scope, $sce, blogPostService) {
	 $scope.totalItems = 1;
	 $scope.currentPage = 1;
	 
	 getBlogPosts($scope, $sce, blogPostService, {});
	 
	  $scope.blog = {
			  posts : []
	  };
	  
	  $scope.pageChanged = function() {
		  getBlogPosts($scope, $sce, blogPostService, {page: $scope.currentPage});
	  };
}

function getBlogPosts($scope, $sce, blogPostService, param) {
	  blogPostService.query(param, function(success, headers) {
				  angular.forEach(success, function(post) {
					post.content = $sce.trustAsHtml(post.content);  
				  });
				  $scope.blog.posts = success;
				  $scope.totalItems = headers("X-WP-Total");
			  }, 
			  function(error) {
				  console.log("error");
			  });
}
