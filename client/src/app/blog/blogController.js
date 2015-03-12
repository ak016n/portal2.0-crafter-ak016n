(function() {
    'use strict';
angular.module('blog').controller('BlogCtrl', BlogCtrl);

BlogCtrl.$inject = ['$scope', '$sce', 'blogService', '$state', 'flashMessageService'];

function BlogCtrl($scope, $sce, blogService, $state, flashMessageService) {
	 init($scope, $sce, blogService, $state, flashMessageService);
	 
	  $scope.pageChanged = function() {
		  getBlogPosts($scope, $sce, blogService.posts(), {page: $scope.pagination.currentPage}, flashMessageService);
	  };
	  
	  $scope.categorySelected = function(category) {
		  getBlogPosts($scope, $sce, blogService.posts(), {"filter[category_name]" : category}, flashMessageService);
	  };
	  
	  $scope.searchSelected = function() {
		  $state.transitionTo('blog.search', {s :  $scope.blog.search.term});
	  };
	  
	  $scope.postComment = function(postId, parentId) {
		  var comment = {content : $scope.blog.comment.content, type : 'comment', parent: parentId };
		  postComment($scope, blogService.comments(), postId, comment, flashMessageService).then( function () {
				getComments($scope, $sce, blogService.comments(), $state, flashMessageService);
		  });
		  $scope.blog.comment.content = '';
	  };
	  
	  $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
		  if(  !$state.params.skip && ! $scope.blog.inProgress && (angular.isUndefinedOrNull($scope.blog.post) || $scope.blog.post.ID != $state.params.id)) {
			  $state.reload();  
		  }
		  
	  });
}

function init($scope, $sce, blogService, $state, flashMessageService) {
		$scope.flashMessages = flashMessageService;
	
		$scope.pagination = {
			totalItems : 1,
			currentPage : 1
		};

		$scope.blog = {
			posts : [],
			inProgress : true
		};
	
		getCategories($scope, blogService.categories(), flashMessageService);	

		if(blogService.getView() === "blog.list") {
			 getBlogPosts($scope, $sce, blogService.posts(), {}, flashMessageService);
		} else if(blogService.getView() === "blog.entry") {
			getPost($scope, $sce, blogService.posts(), $state, flashMessageService);
			getComments($scope, $sce, blogService.comments(), $state, flashMessageService);
		} else {
			getBlogPosts($scope, $sce, blogService.posts(), {"filter[s]" : $state.params.s}, flashMessageService);
		}
}

function getBlogPosts($scope, $sce, blogPostService, param, flashMessageService) {
	  blogPostService.query(param, function(success, headers) {
				  angular.forEach(success, function(post) {
					post.content = $sce.trustAsHtml(post.content);  
				  });
				  $scope.blog.posts = success;
				  $scope.pagination.totalItems = headers("X-WP-Total");
				  $scope.blog.inProgress = false;
			  }, 
			  function(error) {
				  flashMessageService.setError(true);
				  flashMessageService.setMessage(error.data.errors);
			  });
}

function getCategories($scope, blogCategoriesService, flashMessageService) {
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
				  flashMessageService.setMessage(error.data.errors);
			  });
}

function postComment($scope, blogCommentsService, postId, comment, flashMessageService) {
	 var promise = blogCommentsService.save({postId: postId}, comment).$promise;
	 flashMessageService.clearAll();
	 
	 promise.then(
			  function(success) {
				  flashMessageService.addMessage("Comment has been successfully created.");
			  }, 
			  function(error) {
				  //flashMessageService.setError(true); Not setting because we still want to show the post even if there are comment issues
				  flashMessageService.setMessage(error.data.errors);
			  });
	
	return promise;
}

function getPost($scope, $sce, blogPostService, $state, flashMessageService) {
	blogPostService.get({postId: $state.params.id}).$promise.then(
						  function(success) {
							  success.content = $sce.trustAsHtml(success.content);
							  $scope.blog.post = success;
							  $scope.blog.inProgress = false;
						  }, 
						  function(error) {
							  flashMessageService.setError(true);
							  flashMessageService.setMessage(error.data.errors);
						  });
}

function getComments($scope, $sce, blogCommentService, $state, flashMessageService) {
	blogCommentService.query({postId: $state.params.id}).$promise.then(
			  function(success) {
				  $scope.blog.comments = success;
			  }, 
			  function(error) {
				  //flashMessageService.setError(true); Not setting because we still want to show the post even if there are comment issues
				  flashMessageService.setMessage(error.data.errors);
			  });
}

})();
