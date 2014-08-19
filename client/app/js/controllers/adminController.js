angular.module('portalApp').controller('AdminCtrl', ['$scope', 'adminService', 'adminVersionService', 'globalHandleErrorService', '$sce', '$q', '$http', function(sc, adminService, adminVersionService, globalHandleErrorService, sce, $q, $http) {
  sc.adminProp = {
		  description: 'empty'
  };

  sc.refresh = function(form) {
	  if(form.$invalid) {
		  return;
	  }
	  
	  sc.adminProp.description = '';
	  reset();
	  var action = 'Refresh';
	  console.log('refresh');
	  adminService.get({itemKey: sc.adminProp.itemKey, fieldKey: sc.adminProp.fieldKey}).$promise.then(
			  function(success) {
				  handleSuccess({success: success, action: action, form: form});
			  }, 
			  function(error) {
				  handleError({error: error.data.errors, action: action , formName: 'adminForm'});
			  });
  };
  
  sc.create = function(form) {
	  if(form.$invalid) {
		  return;
	  }
	  
	  reset();
	  var action = 'Create';
	  console.log('create');
	  adminService.save({itemKey: sc.adminProp.itemKey, fieldKey: sc.adminProp.fieldKey, description: sc.adminProp.description}).$promise.then(
			  function(success) {
				  handleSuccess({success: success, action: action, form: form});
			  }, 
			  function(error) {
				  handleError({error: error.data.errors, action: action , formName: 'adminForm'});
			  });
  };
  
  sc.update = function(form) {
	  if(form.$invalid) {
		  return;
	  }
	  
	  reset();
	  var action = 'Update';
	  console.log(action);
	  adminService.update({itemKey: sc.adminProp.itemKey, fieldKey: sc.adminProp.fieldKey, description: sc.adminProp.description}).$promise.then(
			  function(success) {
				  handleSuccess({success: success, action: action, form: form});
			  }, 
			  function(error) {
				  handleError({error: error.data.errors, action: action , formName: 'adminForm'});
			  });
  };
  
  sc.delete = function(form) {
	  if(form.$invalid) {
		  return;
	  }
	  
	  reset();
	  var action = 'Delete';
	  console.log(action);
	  adminService.delete({itemKey: sc.adminProp.itemKey, fieldKey: sc.adminProp.fieldKey}).$promise.then(
			  function(success) {
				  handleSuccess({success: success, action: action, form: form});
			  }, 
			  function(error) {
				  handleError({error: error.data.errors, action: action , formName: 'adminForm'});
			  });
  };
  
  sc.diff = function() {
	  sc.diff.responseText1 = '';
	  sc.diff.responseText2 = '';
	  $q.all([propertyByVersion(sc.fromVersion, 'responseText1'), propertyByVersion(sc.withVersion, 'responseText2')])
	  			.then(function(success) {
	  				var diffResult = lineDiff.diffTwoTexts(sc.diff.responseText1, sc.diff.responseText2);
	  				sc.diff.results = sce.trustAsHtml(diffResult);
	  				console.log('after method invocation : ' + sc.diff.results);
	  				}, function(error) {
	  					console.log("error");
	  				});
  };
  
  sc.searchIK = function(itemKey) {
	  return $http.get('/developer/admin/search/' + itemKey)
	  		 .then(function(res) {
	  			 return res.data;
	  		 });
  };
  
  sc.searchFK = function(fieldKey) {
	  return $http.get('/developer/admin/search/' + sc.adminProp.itemKey + '/' + fieldKey)
	  		 .then(function(res) {
	  			 return res.data;
	  		 });
  };
  
  /** Non-scope methods */
  var handleSuccess = function(response) {
	  console.log('success:' + response.success);
	  response.form.$setPristine();
	  console.log('formName:' + response.success);
	  sc.adminProp.successMessage = response.action + ' completed successfully.';
	  sc.adminProp.itemKey = response.success.itemKey;
	  sc.adminProp.fieldKey = response.success.fieldKey;
	  sc.adminProp.description = response.success.description;
	  version();
  };
  
  var handleError = function(response) {
	  console.log('error.data.errors: ' + response.error);
	  globalHandleErrorService({
          formName: response.formName,
          fieldErrors: response.error
	  });
  };
  
  var reset = function() {
	  globalHandleErrorService.clearAllErrors('adminForm');
	  sc.adminProp.successMessage = '';
  };
  
  var version = function() {
	  adminVersionService.get({itemKey: sc.adminProp.itemKey, fieldKey: sc.adminProp.fieldKey}).$promise.then(
			  function(success) {
				  console.log('version info : ' + success);
				  sc.versions = success.versions;
				  sc.fromVersion = success.versions.length - 1;
			  }, 
			  function(error) {
				  sc.versions = [];
				  console.log('errrrrrrrrrrrrrorrrrrrrrr');
			  });
  };
  
  var propertyByVersion = function(version, variableName) {
	  return adminService.get({itemKey: sc.adminProp.itemKey, fieldKey: sc.adminProp.fieldKey, version: version}).$promise.then(
			  function(success) {
				  console.log('success : ' + success);
				  sc.diff[variableName] = success.description;
				  console.log(variableName + " : done");
			  }, 
			  function(error) {
				  console.log('error propertyByVersion :     ' + error);
				  sc.diff[variableName] = '';
			  });
  };
  
}]);
