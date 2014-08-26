angular.module('security.login').
	config(['$urlRouterProvider', '$stateProvider', function($urlRouterProvider, $stateProvider) {
		$stateProvider.state('login', {
			url: '/login',
			templateUrl: 'common/security/login/login.tpl.html'
		});
}]);