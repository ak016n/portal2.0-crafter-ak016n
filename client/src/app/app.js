var app = angular.module('portalApp',
		[ 'admin', 
		  'security', 
		  'pascalprecht.translate', 
		  'ui.bootstrap',
		  'templates-dist', 
		  'ui.router' ]);

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

app.run(['$rootScope', '$stateParams', 'principal', '$sessionStorage', '$state', '$location', function($rootScope, $stateParams, principal, $sessionStorage, $state, $location) {
	$rootScope.$on('$stateChangeStart', function(event, toState, toStateParams) {
		
		var isAllowed = principal.isAuthorized(toState.permissions);
		if(!isAllowed) {
			if (angular.isUndefined($sessionStorage.accessToken)) {
				event.preventDefault();
				$sessionStorage.destUrl = $location.path();
				$state.go('login');
			} else {
				event.preventDefault();
				console.log('error - permission denied');
				$state.go('error');
			}
		}
	});
}]);

angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null ;
};



