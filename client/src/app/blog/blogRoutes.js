angular.module('blog').
	config(['$urlRouterProvider', '$stateProvider', function($urlRouterProvider, $stateProvider) {
		$stateProvider.state('blog', {
			url: '/blog',
			abstract: true,
			controller: 'BlogCtrl as blogCtrl',
			templateUrl: 'app/blog/blog-base.tpl.html'
		}).state('blog.list', {
			url: '',
			templateUrl: 'app/blog/blog-home.tpl.html',
			resolve: {
				view: ['blogService', function(blogService) {
					blogService.setView('blog.list');
				}]
			}
		}).state('blog.entry', {
			url: "/:id",
			templateUrl: 'app/blog/blog-entry.tpl.html',
			resolve: {
				view: ['blogService', function(blogService) {
					blogService.setView('blog.entry');
				}]
			}
		}).state('blog.search', {
			url: '?s',
			templateUrl: 'app/blog/blog-home.tpl.html',
			resolve: {
				view: ['blogService', function(blogService) {
					blogService.setView('blog.search');
				}]
			}
		});
}]);