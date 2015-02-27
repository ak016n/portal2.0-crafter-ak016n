angular.module('blog').
	config(['$urlRouterProvider', '$stateProvider', function($urlRouterProvider, $stateProvider) {
		$stateProvider.state('blog', {
			url: '/blog',
			abstract: true,
			controller: 'BlogCtrl as blogCtrl',
			templateUrl: 'app/blog/blog-base.tpl.html'
		}).state('blog.list', {
			url: '',
			templateUrl: 'app/blog/blog-home.tpl.html'
		}).state('blog.entry', {
			url: "/:id",
			templateUrl: 'app/blog/blog-entry.tpl.html',
			resolve: {
			post: ['$rootScope','$sce','blogPostService', '$stateParams', function($rootScope, $sce, blogPostService, $stateParams) {
					console.log("invoking resolve");
					return blogPostService.get({postId: $stateParams.id}).$promise.then(
					  function(success) {
						  success.content = $sce.trustAsHtml(success.content);
						  $rootScope.blogPost = success;
						  return success;
					  }, 
					  function(error) {
						  console.log("error");
					  });
				 }]
			}
		});
}]);