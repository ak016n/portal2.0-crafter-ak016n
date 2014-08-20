var adminCtrl = angular.module('adminCtrl.adminService', ['ngResource']);

adminCtrl.factory('adminService', ['$resource', function($resource, $scope) {
		return $resource('/developer/admin/:itemKey/:fieldKey', {}, {
			update: {method: 'PUT'}
	    });
  }]);

adminCtrl.factory('adminVersionService', ['$resource', function($resource, $scope) {
	return $resource('/developer/admin/:itemKey/:fieldKey/versions', {}, {
    });
}]);