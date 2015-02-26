angular.module('blog').controller('BlogCtrl', BlogCtrl);

BlogCtrl.$inject = ['$scope', '$sce', 'blogPostService', 'blogCategoriesService'];

function BlogCtrl($scope, $sce, blogPostService, blogCategoriesService) {
	 $scope.totalItems = 1;
	 $scope.currentPage = 1;
	 
	 getBlogPosts($scope, $sce, blogPostService, {});
	 
	 getCategories($scope, blogCategoriesService);
	 
	  $scope.blog = {
			  posts : []
	  };
	  
	  $scope.pageChanged = function() {
		  getBlogPosts($scope, $sce, blogPostService, {page: $scope.currentPage});
	  };
	  
	  $scope.categorySelected = function(category) {
		  getBlogPosts($scope, $sce, blogPostService, {"filter[category_name]" : category});
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

function getCategories($scope, blogCategoriesService) {
	blogCategoriesService.query({}).$promise.then(
			  function(success) {
				  // slicing data into two column sets
				  var newArr = [];
				  for (var i=0; i<success.length; i+=2) {
				    newArr.push(success.slice(i, i+2));
				  }
				  $scope.categories =  newArr;
			  }, 
			  function(error) {
				  console.log("error");
			  });
}
