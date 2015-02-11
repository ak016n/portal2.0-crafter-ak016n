angular.module('portalApp').controller('NavigationCtrl', ['$scope', '$location', function($scope, $location) {
	 $scope.isActive = function (viewLocation) { 
	        return viewLocation === $location.path();
	 };
}]);