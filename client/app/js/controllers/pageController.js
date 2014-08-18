function PageCtrl($scope, $http) {
    $http.get('/developer/content').
        success(function(data) {
        	$scope.content = data;
        });
}