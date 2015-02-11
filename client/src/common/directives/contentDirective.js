angular.module('portalApp').directive('contentItem', ['$http', '$compile', '$templateCache', function ($http, $compile, $templateCache) {
    return {
        restrict: "E",
        replace: true,
        template: '<div class="controls"></div>',
        link: function(scope, element, attrs) {
        	var template = $compile($templateCache.get('app/content/' + scope.content.templateUrl))(scope);
            element.append(template);
        },
        scope: {
            content:'='
        }
    };
}]);
