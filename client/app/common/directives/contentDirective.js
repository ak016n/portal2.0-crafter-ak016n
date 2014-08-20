angular.module('portalApp').directive('contentItem', ['$http', '$compile', function ($http, $compile) {

    var getTemplate = function(url) {
        var template = '';
        var finalUrl = '/developer/views/partials/' + url;
        $http.get(finalUrl).then(function(templateHtml) {
        	template = templateHtml.data;
        });
        return template;
    };

    var linker = function(scope, element, attrs) {
        element.html(getTemplate(scope.content.templateUrl)).show();
        $compile(element.contents())(scope);
    };

    return {
        restrict: "E",
        replace: true,
        template: '<div class="controls"></div>',
        link: function(scope, element, attrs) {
        	var template = '';
        	var finalUrl = '/developer/views/partials/' + scope.content.templateUrl;
            $http.get(finalUrl).then(function(templateHtml) {
            	template = $compile(templateHtml.data)(scope);
            	element.append(template);
            });
        },
        scope: {
            content:'='
        }
    };
}]);
