(function() {
    'use strict';
    
	angular.module('portalApp').factory('globalHandleErrorService', ['$translate', '$log', function($translate, $log) {
		  var formColl = [];
	
		  // Used to set server side errors
		  var globalHandleErrorService = function(opts) {
		    var fieldErrors = opts.fieldErrors;
		    var ctrl = formColl[opts.formName];
	
		    if(angular.isUndefinedOrNull(fieldErrors)) {
		    	return;
		    }
		    
		    Object.keys(fieldErrors).forEach(function(index) {
		      $log.info('translated fieldErrorKey : ' + $translate.instant(fieldErrors[index].id) + ' fieldErrors : ' + $translate.instant(fieldErrors[index].message));
		      ctrl.addError($translate.instant(fieldErrors[index].id), $translate.instant(fieldErrors[index].message));
		    });
		  };
	
		  globalHandleErrorService.registerForm = function(formName, ctrl) {
			  formColl[formName] = ctrl;
		  };
		  
		  globalHandleErrorService.clearAllErrors = function(formName) {
			  if(!angular.isUndefinedOrNull(formColl[formName])) {
				  formColl[formName].clearAllErrors(); 
			  }
		  };
	
		  return globalHandleErrorService;
		}]);

})();