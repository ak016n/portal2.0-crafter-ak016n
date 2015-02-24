angular.module('blog').
	config(['$urlRouterProvider', '$stateProvider', function($urlRouterProvider, $stateProvider) {
		$stateProvider.state('blog', {
			url: '/blog',
			templateUrl: 'app/blog/blog-home.tpl.html'
		});
}]);