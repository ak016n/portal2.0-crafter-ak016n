(function() {
    'use strict';

	angular.module('portalApp').controller('NavigationCtrl', ['$scope', '$location', function($scope, $location) {
		 $scope.isActive = function (viewLocation) {
			// if wildcard asterisk is used (to include any subfolders under the provided path)
			if(viewLocation.slice(-2) === "/*") {
				return $location.path().lastIndexOf(viewLocation.slice(0,viewLocation.length-2), 0) === 0;
			}
			else {
				return viewLocation === $location.path();
			}
		 };
	}]);

})();