(function() {
    'use strict';
angular.module('portalApp').factory('flashMessageService', ['$translate', '$rootScope', function($translate, $rootScope) {
	
	  var isError = false;
	  var messageColl = [];
	  
	  $rootScope.$on('$stateChangeStart', 
			  function(event, toState, toParams, fromState, fromParams){
		  messageColl = [];
		  isError = false;
	  });
	  
	  return {
		  setMessage: function(messages) {
			    angular.forEach(messages, function(value) {
				      messageColl.push($translate.instant(value.message));
				 });
		  },
		  getMessage: function() {
		      return messageColl;
		  },
		  addMessage: function(message) {
			  messageColl.push($translate.instant(value));
		  },
		  setError: function(status) {
			  isError = status;
		  },
		  isError: function() {
			 return isError;
		  }
	  };
}]);

})();