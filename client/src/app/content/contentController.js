angular.module('content').controller('ContentCtrl', ['$scope', 'contentService', '$location', function(sc, contentService, location) {
	var subPath = location.url().replace('/','');
	contentService.get({subPath:subPath}).$promise.then(
			  function(success) {
				  sc.content = success;
			  });
}]);