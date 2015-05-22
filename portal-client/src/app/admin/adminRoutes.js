(function() {
    'use strict';
	angular.module('admin').
		config(['$urlRouterProvider', '$stateProvider', function($urlRouterProvider, $stateProvider) {
			$stateProvider.state('admin', {
				url: '/admin',
				templateUrl: 'app/admin/admin-properties.tpl.html',
				permissions: ['ROLE_SYS_ADMIN']
			});
	}]);
})();