(function() {
    'use strict';
angular.module('blog').controller('BlogCtrl', BlogCtrl);

BlogCtrl.$inject = ['$scope', '$sce', 'blogPostService', 'blogCategoriesService', 'blogCommentService'];

function BlogCtrl($scope, $sce, blogPostService, blogCategoriesService, blogCommentService) {
	 init($scope, $sce, blogPostService,blogCategoriesService);
	 
	  $scope.pageChanged = function() {
		  getBlogPosts($scope, $sce, blogPostService, {page: $scope.pagination.currentPage});
	  };
	  
	  $scope.categorySelected = function(category) {
		  getBlogPosts($scope, $sce, blogPostService, {"filter[category_name]" : category});
	  };
	  
	  $scope.searchSelected = function() {
		  getBlogPosts($scope, $sce, blogPostService, {"filter[s]" : $scope.blog.search.term});
	  };
	  
	  $scope.postComment = function(postId, parentId) {
		  var comment = {content : $scope.blog.comment, type : 'comment', parent: parentId };
		  postComment($scope, blogCommentService, postId, comment);
	  };
}

function init($scope, $sce, blogPostService, blogCategoriesService) {
		$scope.pagination = {
				totalItems : 1,
				currentPage : 1
		}

		$scope.blog = {
				  posts : []
		  };
		 getBlogPosts($scope, $sce, blogPostService, {});
		 getCategories($scope, blogCategoriesService);	
}

function getBlogPosts($scope, $sce, blogPostService, param) {
	  blogPostService.query(param, function(success, headers) {
				  angular.forEach(success, function(post) {
					post.content = $sce.trustAsHtml(post.content);  
				  });
				  $scope.blog.posts = success;
				  $scope.pagination.totalItems = headers("X-WP-Total");
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
				  $scope.blog.categories =  newArr;
			  }, 
			  function(error) {
				  console.log("error");
			  });
}

function postComment($scope, blogCommentsService, postId, comment) {
	blogCommentsService.save({postId: postId}, comment).$promise.then(
			  function(success) {
				  console.log("success");
			  }, 
			  function(error) {
				  console.log("error");
			  });
}

})();
