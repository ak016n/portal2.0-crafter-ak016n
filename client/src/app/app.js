var app = angular.module('portalApp', ['adminCtrl.adminService', 'pascalprecht.translate', 'ui.bootstrap', 'templates-dist', 'ui.router']);

app.config(function($translateProvider) {
	$translateProvider.useUrlLoader('/developer/i18N');
	$translateProvider.preferredLanguage('en');
});

app.config(['$urlRouterProvider', '$stateProvider', function($urlRouterProvider, $stateProvider) {
	$urlRouterProvider.otherwise('/');
	
	$stateProvider.state('home', {
		url: '/',
		template: 'home'
	});
}]);

angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null ;
};



