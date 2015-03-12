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
	  
	  function filterDupAndAdd(value) {
	    	var message = $translate.instant(value);
	    	if(messageColl.indexOf(message) == -1) {
	    		messageColl.push(message);
	    	}
	  }
	  
	  return {
		  setMessage: function(messages) {
			    angular.forEach(messages, function(value) {
			    	filterDupAndAdd(value.message);
				 });
		  },
		  getMessage: function() {
		      return messageColl;
		  },
		  addMessage: function(message) {
			  filterDupAndAdd(message);
		  },
		  setError: function(status) {
			  isError = status;
		  },
		  isError: function() {
			 return isError;
		  },
		  clearAll: function() {
			  messageColl = [];
		  }
	  };
}]);

})();