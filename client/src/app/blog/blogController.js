angular.module('blog').controller('BlogCtrl', BlogCtrl);

BlogCtrl.$inject = ['$scope', '$sce', 'blogPostService'];

function BlogCtrl($scope, $sce, blogPostService) {
	  $scope.blog = {
			  posts : []
	  };
	  
	  blogPostService.query({}).$promise.then(
			  function(success) {
				  angular.forEach(success, function(post) {
					post.content = $sce.trustAsHtml(post.content);  
				  });
				  $scope.blog.posts = success;
				  
			  }, 
			  function(error) {
				  console.log("error");
			  });
}
