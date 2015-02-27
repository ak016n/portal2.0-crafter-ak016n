(function() {
    'use strict';
angular.module('blog').controller('BlogCtrl', BlogCtrl);

BlogCtrl.$inject = ['$scope', '$sce', 'blogService', '$state'];

function BlogCtrl($scope, $sce, blogService, $state) {
	 init($scope, $sce, blogService, $state);
	 
	  $scope.pageChanged = function() {
		  getBlogPosts($scope, $sce, blogService.posts(), {page: $scope.pagination.currentPage});
	  };
	  
	  $scope.categorySelected = function(category) {
		  getBlogPosts($scope, $sce, blogService.posts(), {"filter[category_name]" : category});
	  };
	  
	  $scope.searchSelected = function() {
		  getBlogPosts($scope, $sce, blogService.posts(), {"filter[s]" : $scope.blog.search.term});
	  };
	  
	  $scope.postComment = function(postId, parentId) {
		  var comment = {content : $scope.blog.comment.content, type : 'comment', parent: parentId };
		  postComment($scope, blogService.comments(), postId, comment).then( function () {
				getComments($scope, $sce, blogService.comments(), $state)
		  });
		  $scope.blog.comment.content = '';
	  };
}

function init($scope, $sce, blogService, $state) {
		$scope.pagination = {
				totalItems : 1,
				currentPage : 1
		}

		$scope.blog = {
				  posts : []
		  };
		
		if(blogService.getView() === "blog.list") {
			 getBlogPosts($scope, $sce, blogService.posts(), {});
			 getCategories($scope, blogService.categories());	
		} else {
			getPost($scope, $sce, blogService.posts(), $state);
			getComments($scope, $sce, blogService.comments(), $state);
		}
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
	 var promise = blogCommentsService.save({postId: postId}, comment).$promise;
	
	 promise.then(
			  function(success) {
				  console.log("success");
			  }, 
			  function(error) {
				  console.log("error");
			  });
	
	return promise;
}

function getPost($scope, $sce, blogPostService, $state) {
	blogPostService.get({postId: $state.params.id}).$promise.then(
						  function(success) {
							  success.content = $sce.trustAsHtml(success.content);
							  $scope.blog.post = success;
						  }, 
						  function(error) {
							  console.log("error");
						  });
}

function getComments($scope, $sce, blogCommentService, $state) {
	blogCommentService.query({postId: $state.params.id}).$promise.then(
			  function(success) {
				  $scope.blog.comments = success;
			  }, 
			  function(error) {
				  console.log("error");
			  });
}

})();
