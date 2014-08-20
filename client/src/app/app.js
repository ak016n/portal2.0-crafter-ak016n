var app = angular.module('portalApp', ['adminCtrl.adminService', 'pascalprecht.translate', 'ui.bootstrap', 'templates-dist']);

app.config(function($translateProvider) {
	$translateProvider.useUrlLoader('/developer/i18N');
	$translateProvider.preferredLanguage('en');
});


angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null ;
};



