/**
 * Http Interceptor for authentication request - 401
 */
angular.module('security').config(['$httpProvider', function($httpProvider) {
	$httpProvider.interceptors.push(['$q', '$rootScope', '$location', '$sessionStorage', function($q, $rootScope, $location, $sessionStorage) {
		return {
			'responseError' : function(rejection) {
				var status = rejection.status;
				var config = rejection.config;
				var method = config.method;
				var url = config.url;

				if (angular.isUndefined($sessionStorage.accessToken) && status === 401) {
					$location.path("/login");
				} else {
					$rootScope.error = method + " on " + url + " failed with status " + status;
				}

				return $q.reject(rejection);
			},
			request : function(config) {
				if(angular.isDefined($sessionStorage.accessToken)) {
					config.headers = {'Authorization' : 'Bearer ' + $sessionStorage.accessToken};
				}
				return config;
			}
		};
	}]);
}]);