(function() {
    'use strict';
	angular.module('security.login')
		.controller('LoginController', ['$scope', 'loginService', 'globalHandleErrorService', '$sessionStorage', '$location', 'principal', function(sc, loginService, globalHandleErrorService, $sessionStorage, $location, principal) {
			console.log("login controller");
			
			sc.login = function(form) {
				  if(form.$invalid) {
					  return;
				  }
				  
				  loginService.login($.param({username: sc.login.username, password: sc.login.password, grant_type: 'password', scope: 'read write trust'})).$promise.then(
						  function(success) {
							  $sessionStorage.accessToken = success.access_token;
							  $sessionStorage.refreshToken = success.refresh_token;
							  principal.set(success.principal);
							  $location.path($sessionStorage.destUrl);
							  console.log('success in attempted login :' + success.access_token);
						  }, 
						  function(error) {
							  handleError({error: error.data.errors, formName: 'loginForm'});
						  });
			  };
			  
			  var handleError = function(response) {
				  console.log('error.data.errors: ' + response.error);
				  globalHandleErrorService({
			          formName: response.formName,
			          fieldErrors: response.error
				  });
			  };
		  
	}]);
})();
