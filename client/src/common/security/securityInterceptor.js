(function() {
    'use strict';

	/**
	 * Http Interceptor for authentication request - 401
	 */
	angular.module('security').config(['$httpProvider', function($httpProvider, $stateProvider) {
		$httpProvider.interceptors.push(['$q', '$rootScope', '$sessionStorage', '$location', function($q, $rootScope, $sessionStorage, $location) {
			return {
				'responseError' : function(rejection) {
					var status = rejection.status;
					var config = rejection.config;
					var method = config.method;
					var url = config.url;
	
					if (angular.isUndefined($sessionStorage.accessToken) && status === 401) {
						$sessionStorage.destUrl = $location.path();
						$location.path("/login");
					} else {
						$rootScope.error = method + " on " + url + " failed with status " + status;
					}
	
					return $q.reject(rejection);
				},
				request : function(config) {
					if(angular.isDefined($sessionStorage.accessToken)) {
						config.headers.Authorization = 'Bearer ' + $sessionStorage.accessToken;
					}
					return config;
				}
			};
		}]);
	}]);

})();