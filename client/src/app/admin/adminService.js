(function() {
    'use strict';
	angular.module('admin').factory('adminService', ['$resource', function($resource, $scope) {
			return $resource('/developer/admin/:itemKey/:fieldKey', {}, {
				update: {method: 'PUT'}
		    });
	  }]);
	
	angular.module('admin').factory('adminVersionService', ['$resource', function($resource, $scope) {
		return $resource('/developer/admin/:itemKey/:fieldKey/versions', {}, {
	    });
	}]);
})();