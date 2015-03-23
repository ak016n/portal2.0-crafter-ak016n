(function() {
    'use strict';
	angular.module('security.login')
		.factory('loginService', ['$resource', function($resource, $scope) {
			return $resource('/developer/oauth/token', {}, {
				login: {method: 'POST', headers : {'Authorization' : 'Basic dHJ1c3RlZF9pbnRlcm5hbF9jbGllbnRfd2l0aF91c2VyOnNvbWVzZWNyZXRfdGljd3U=', 'Content-Type': 'application/x-www-form-urlencoded'}}
		    });
	  }]);
})();