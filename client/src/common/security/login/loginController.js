angular.module('security.login')
	.controller('LoginController', ['$scope', 'loginService', 'globalHandleErrorService', function(sc, loginService, globalHandleErrorService) {
		console.log("login controller");
		
		sc.login = function(form) {
			  if(form.$invalid) {
				  return;
			  }
			  
			  loginService.login($.param({username: sc.login.username, password: sc.login.password, grant_type: 'password', scope: 'read write trust'})).$promise.then(
					  function(success) {
						  console.log('success in attempted login');
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