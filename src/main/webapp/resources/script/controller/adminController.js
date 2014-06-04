var adminCtrl = angular.module('portalApp');

adminCtrl.controller('GetAdminPropertyCtrl', ['$scope', 'adminService', '$translate', 'globalHandleErrorService', function(sc, adminService, $translate, globalHandleErrorService) {
  sc.adminProp = {
		  description: 'empty'
  };

  sc.refresh = function(form) {
	  if(form.$invalid) {
		  console.log('form will not be submitted since it is invalid');
		  return;
	  }
	  console.log('refresh');
	  adminService.get({itemKey: sc.adminProp.itemKey, fieldKey: sc.adminProp.fieldKey}).$promise.then(
			  function(success) {
				  console.log(success);
				  sc.adminProp.itemKey = success.itemKey;
				  sc.adminProp.fieldKey = success.fieldKey;
				  sc.adminProp.description = success.description;
			  }, 
			  function(error) {
				  sc.adminProp.description = '';
				  console.log(error);
				  globalHandleErrorService({
			          formName: 'adminForm',
			          fieldErrors: error.data.errors
				  });
			  });
  };
  sc.create = function() {
	  sc.submitted = true;
	  console.log('create');
  };
  sc.triggerErrHandling = function() {
	  console.log('error handling');
	  processServerErrorMessage();
  };
}]);
