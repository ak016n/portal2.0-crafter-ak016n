angular.module('content').
	config(['$urlRouterProvider', '$stateProvider', function($urlRouterProvider, $stateProvider) {
		$stateProvider.state('faq', {
			url: '/faq',
			templateUrl: 'app/content/faq.tpl.html'
		});
}]);