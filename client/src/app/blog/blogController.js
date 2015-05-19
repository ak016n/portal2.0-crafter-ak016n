(function() {
    'use strict';
	angular.module('blog').controller('BlogCtrl', BlogCtrl);
	
	BlogCtrl.$inject = ['$scope', '$sce', 'blogService', '$state', 'flashMessageService'];
	
	function BlogCtrl($scope, $sce, blogService, $state, flashMessageService) {
		init($scope, $sce, blogService, $state, flashMessageService);
		 
		$scope.pageChanged = function() {
			getBlogPosts($scope, $sce, blogService.posts(), {"filter[posts_per_page]" : $scope.blog.postPerPage, page: $scope.pagination.currentPage}, flashMessageService);
		};

		$scope.pagerChanged = function() {
			getCategories($scope, blogService.categories(), flashMessageService);
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
				currentPage : 1,
				maxSize : 5
			};
			
			$scope.pager = {
				totalItems : 1,
				currentPage : 1,
				itemsPerPage : 15
			};
	
			$scope.blog = {
				posts : [],
				postPerPage : 5,
				inProgress : true
			};
		
			getCategories($scope, blogService.categories(), flashMessageService);
			getTags($scope, blogService.tags(), flashMessageService);
			getUsers($scope, blogService.users());
	
			switch(blogService.getView()) {
				case "blog.list":
					 getBlogPosts($scope, $sce, blogService.posts(), {"filter[posts_per_page]" : $scope.blog.postPerPage}, flashMessageService);
					 break;
				case "blog.entry":
					getPost($scope, $sce, blogService.posts(), $state, flashMessageService).then( function () {
						getComments($scope, $sce, blogService.comments(), $state, flashMessageService);
					  });
					break;
				case "blog.list.categories":	
					getBlogPosts($scope, $sce, blogService.posts(), {"filter[category_name]" : $state.params.category}, flashMessageService);
					break;
				case "blog.list.tags":	
					getBlogPosts($scope, $sce, blogService.posts(), {"filter[tag]" : $state.params.tag}, flashMessageService);
					break;
				default:
					getBlogPosts($scope, $sce, blogService.posts(), {"filter[s]" : $state.params.s}, flashMessageService);
					break;
			}
	}
	
	function getBlogPosts($scope, $sce, blogPostService, param, flashMessageService) {
		  $scope.loading = true;
		  blogPostService.query(param, function(success, headers) {
					  angular.forEach(success, function(post) {
						post.content = $sce.trustAsHtml(post.content);  
					  });
					  $scope.blog.posts = success;
					  $scope.pagination.totalItems = headers("X-WP-Total");
					  $scope.blog.inProgress = false;
					  $scope.loading = false;
				  }, 
				  function(error) {
					  flashMessageService.setError(true);
					  flashMessageService.setMessage(error.data.errors);
					  $scope.loading = false;
				  });
	}
	
	function getCategories($scope, blogCategoriesService, flashMessageService) {
		blogCategoriesService.query({}).$promise.then(
			function(success) {
				var pager_begin = (($scope.pager.currentPage - 1) * $scope.pager.itemsPerPage);
				var pager_end = pager_begin + $scope.pager.itemsPerPage;
				if(success.length < pager_end) {
					pager_end = success.length;
				}

				$scope.blog.categories = success.slice(pager_begin,pager_end);
				$scope.pager.totalItems = success.length;
			}, 
			function(error) {
				flashMessageService.setMessage(error.data.errors);
			});
	}
	
	function getTags($scope, blogTagService, flashMessageService) {
		blogTagService.query({}).$promise.then(
			function(success) {
				// slicing data into two column sets
				var newArr = [];
				for (var i=0; i<success.length; i+=2) {
					newArr.push(success.slice(i, i+2));
				}
				$scope.blog.tags =  newArr;
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
		var promise = blogPostService.get({postId: $state.params.slug}).$promise;
		$scope.loading = true;
		promise.then(
				  function(success) {
					  success.content = $sce.trustAsHtml(success.content);
					  $scope.blog.post = success;
					  $scope.blog.inProgress = false;
				  }, 
				  function(error) {
					  flashMessageService.setError(true);
					  flashMessageService.setMessage(error.data.errors);
				  }).finally(function() {
					    $scope.loading = false;
				  });
					
		return promise;
	}
	
	function getComments($scope, $sce, blogCommentService, $state, flashMessageService) {
		var postId = $state.params.id !== null ? $state.params.id : $scope.blog.post.ID;
		blogCommentService.query({postId: postId}).$promise.then(
			function(success) {
				$scope.blog.comments = success;
			}, 
			function(error) {
				//flashMessageService.setError(true); Not setting because we still want to show the post even if there are comment issues
				flashMessageService.setMessage(error.data.errors);
			});
	}
	
	function getUsers($scope, blogUserService) {
		blogUserService.get({}).$promise.then(
			function(success) {
				$scope.$root.user = success;
			},
			function(error) {
				flashMessageService.setMessage(error.data.errors);
			});
	}
})();