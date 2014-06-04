var adminCtrl = angular.module('adminCtrl.adminService', ['ngResource']);

adminCtrl.factory('adminService', ['$resource', function($resource, $scope) {
		return $resource('/developer/admin/:itemKey/:fieldKey', {}, {
	    	 query: {method:'GET', params:{}, isArray:true}
	    });
  }]);