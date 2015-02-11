angular.module('content').factory('contentService', ['$resource', function($resource) {
    return $resource('/developer/content/:subPath');
}]);