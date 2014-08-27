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

angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null ;
};



;angular.module('portalApp').directive('contentItem', ['$http', '$compile', function ($http, $compile) {

    var getTemplate = function(url) {
        var template = '';
        var finalUrl = '/developer/views/partials/' + url;
        $http.get(finalUrl).then(function(templateHtml) {
        	template = templateHtml.data;
        });
        return template;
    };

    var linker = function(scope, element, attrs) {
        element.html(getTemplate(scope.content.templateUrl)).show();
        $compile(element.contents())(scope);
    };

    return {
        restrict: "E",
        replace: true,
        template: '<div class="controls"></div>',
        link: function(scope, element, attrs) {
        	var template = '';
        	var finalUrl = '/developer/views/partials/' + scope.content.templateUrl;
            $http.get(finalUrl).then(function(templateHtml) {
            	template = $compile(templateHtml.data)(scope);
            	element.append(template);
            });
        },
        scope: {
            content:'='
        }
    };
}]);
;angular.module('portalApp').directive('attFormErrors', ['$log', 'globalHandleErrorService', function ($log, globalHandleErrorService) {
    return {
        restrict: "A",
        replace: false,
        controller: ['$scope', '$element', function($scope, $element) {
        	var ctrls = {};
        	var formLevelErrorsId = 'top_lvl_errors';
        	
        	this.addCtrl = function(fieldName, ctrl) {
        		ctrls[fieldName] = ctrl;
        	};
        	
        	this.addError = function(fieldName, error) {
        		if (!(fieldName in ctrls)) return;
                return ctrls[fieldName].addError(fieldName, error);
        	};
        	
        	this.clearErrorsFor = function(fieldName) {
        	    if (!(fieldName in ctrls)) return;
        	    return ctrls[fieldName].clearErrors();
        	};

        	this.clearFormLevelErrors = function() {
        		this.clearErrorsFor(formLevelErrorsId);
        	};
        	
        	this.clearAllErrors = function() {
        		angular.forEach(ctrls, function(value, key){
        			ctrls[key].clearErrors();
        		});
        	};
        }],
        
        link: function(scope, element, attr, ctrl) {
        	globalHandleErrorService.registerForm(attr.name, ctrl);
        }
    };
}]);


angular.module('portalApp').directive('attFieldErrors', ['$log', '$compile', function ($log, $compile) {
	
	var errors = {
	};
	
	var getTemplate = function() {
		return '<label class="control-label" ng-repeat="(key, value) in errorMap" ng-show="validateOnSubmit">{{value | translate}}</label>';
	};
	
    return {
    	scope: true,
        restrict: "A",
        replace: false,
        require: ['?ngModel', 'attFieldErrors', '^attFormErrors'],
        controller: ['$scope', function($scope) {
            $scope.errorMap = {};
            
            this.addError = function(field, error) {
              $scope.errorMap[field] = error;
            };

            this.removeError = function(field) {
            	delete $scope.errorMap[field];
            };
            
            this.clearErrors = function() {
              $scope.errorMap = {};
            };
            
          }],
        link: function(scope, element, attrs, ctrl) {
        	var ngModelCtrl = ctrl[0];
        	var attFieldErrorsCtrl = ctrl[1];
        	var attFormErrorsCtrl = ctrl[2];
        	
        	attFormErrorsCtrl.addCtrl(attrs.name, attFieldErrorsCtrl);

        	var el = getTemplate();
        	compiled = $compile(el)(scope);
        	
        	if(element.parent().find('label').length === 0) {
        		element.parent().parent().find('label').after(compiled);
        	} else {
        		element.parent().find('label').after(compiled);
        	}
        	
        	var clearFormLevelErrors = function() {
        		attFormErrorsCtrl.clearFormLevelErrors();
        	};
        	
        	if(!ngModelCtrl) {
        		return;
        	}
        	
        	scope.$watch(function () {
                return ngModelCtrl.$viewValue;
             }, function(newValue) {
            	 $log.info("scope.validateOnSubmit : " + scope.validateOnSubmit);
            	 clearFormLevelErrors();
                 if (ngModelCtrl.$invalid) {
                	 for(var key in ngModelCtrl.$error) {
                		 $log.info('key: ' + key + ' value: ' + ngModelCtrl.$error[key]);
                		 var tempFieldKey = attrs.name + '.' + key;
                		 if(ngModelCtrl.$error[key]) {
                			 attFieldErrorsCtrl.addError(tempFieldKey, tempFieldKey);
                		 } else {
                			 attFieldErrorsCtrl.removeError(tempFieldKey);
                		 }
                	 }
               	  	 $log.info('Data : ' +  ngModelCtrl.$viewValue +' ,ngModelCtrl error: ' + ngModelCtrl.$error);
                 } else if (ngModelCtrl.$valid) {
                  attFieldErrorsCtrl.clearErrors();	 
               	  $log.info('ngModelCtrl error: no errors');
                 }
             });
        }
    };
}]);;angular.module('security.login', ['ui.router', 'ngResource']);;angular.module('security.login')
	.controller('LoginController', ['$scope', 'loginService', 'globalHandleErrorService', '$sessionStorage', '$location', function(sc, loginService, globalHandleErrorService, $sessionStorage, $location) {
		console.log("login controller");
		
		sc.login = function(form) {
			  if(form.$invalid) {
				  return;
			  }
			  
			  loginService.login($.param({username: sc.login.username, password: sc.login.password, grant_type: 'password', scope: 'read write trust'})).$promise.then(
					  function(success) {
						  $sessionStorage.accessToken = success.access_token;
						  $sessionStorage.refreshToken = success.refresh_token;
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
	  
}]);;angular.module('security.login').
	config(['$urlRouterProvider', '$stateProvider', function($urlRouterProvider, $stateProvider) {
		$stateProvider.state('login', {
			url: '/login',
			templateUrl: 'common/security/login/login.tpl.html'
		});
}]);;angular.module('security.login')
	.factory('loginService', ['$resource', function($resource, $scope) {
		return $resource('/developer/oauth/token', {}, {
			login: {method: 'POST', headers : {'Authorization' : 'Basic dHJ1c3RlZF9pbnRlcm5hbF9jbGllbnRfd2l0aF91c2VyOnNvbWVzZWNyZXRfdGljd3U=', 'Content-Type': 'application/x-www-form-urlencoded'}}
	    });
  }]);;angular.module('security', ['security.login', 'ngStorage']);;/**
 * Http Interceptor for authentication request - 401
 */
angular.module('security').config(['$httpProvider', function($httpProvider, $stateProvider) {
	$httpProvider.interceptors.push(['$q', '$rootScope', '$sessionStorage', '$location', function($q, $rootScope, $sessionStorage, $location) {
		return {
			'responseError' : function(rejection) {
				var status = rejection.status;
				var config = rejection.config;
				var method = config.method;
				var url = config.url;

				if (angular.isUndefined($sessionStorage.accessToken) && status === 401) {
					$sessionStorage.destUrl = $location.path();
					$location.path("/login");
				} else {
					$rootScope.error = method + " on " + url + " failed with status " + status;
				}

				return $q.reject(rejection);
			},
			request : function(config) {
				if(angular.isDefined($sessionStorage.accessToken)) {
					config.headers = {'Authorization' : 'Bearer ' + $sessionStorage.accessToken};
				}
				return config;
			}
		};
	}]);
}]);;;var lineDiff = (function() {
	return {
		diffTwoTexts : function(text1, text2) {
			var dmp = new diff_match_patch();
			console.log('text1 : ' + text1 + ' ' + 'text2 : ' + text2);
			var a = dmp.diff_linesToChars_(text2, text1);
			var lineText1 = a.chars1;
			var lineText2 = a.chars2;
			var lineArray = a.lineArray;

			var diffs = dmp.diff_main(lineText1, lineText2, false);

			dmp.diff_charsToLines_(diffs, lineArray);
			var prettyHtml = createPrettyHTML.withDiff(diffs);
			//dmp.diff_cleanupSemantic(diffs);
			console.log('prettyHTML : ' + prettyHtml);
			return prettyHtml;
		}	
	};
	
})();

var createPrettyHTML = (function() {
	var pattern_amp = /&/g;
	var pattern_lt = /</g;
	var pattern_gt = />/g;
	var pattern_para = /\n/g;
	var div_ins_start = '<div style="color:green">';
	var div_delete_start = '<div style="color:red">';
	var div_end = '</div>';
	
	return {
		withDiff: function(diffs) {
			var html = [];
			for (var x = 0; x < diffs.length; x++) {
				var op = diffs[x][0]; // Operation (insert, delete, equal)
				var data = diffs[x][1]; // Text of change.
				var text = data.replace(pattern_amp, '&amp;').replace(pattern_lt,
						'&lt;').replace(pattern_gt, '&gt;').replace(pattern_para,
						'<br>');
				switch (op) {
				case DIFF_INSERT:
					html[x] = div_ins_start + text + div_end;
					break;
				case DIFF_DELETE:
					html[x] = div_delete_start + text + div_end;
					break;
				case DIFF_EQUAL:
					html[x] = '<span>' + text + '</span>';
					break;
				}
			}
			return html.join('');
		}
	};
})();
;angular.module('portalApp').factory('globalHandleErrorService', ['$translate', '$log', function($translate, $log) {
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
	}]);;angular.module('admin', ['ngResource', 'ui.router']);;angular.module('admin').controller('AdminController', ['$scope', 'adminService', 'adminVersionService', 'globalHandleErrorService', '$sce', '$q', '$http', function(sc, adminService, adminVersionService, globalHandleErrorService, sce, $q, $http) {
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
;angular.module('admin').
	config(['$urlRouterProvider', '$stateProvider', function($urlRouterProvider, $stateProvider) {
		$stateProvider.state('admin', {
			url: '/admin',
			templateUrl: 'app/admin/admin-properties.tpl.html'
		});
}]);;angular.module('admin').factory('adminService', ['$resource', function($resource, $scope) {
		return $resource('/developer/admin/:itemKey/:fieldKey', {}, {
			update: {method: 'PUT'}
	    });
  }]);

angular.module('admin').factory('adminVersionService', ['$resource', function($resource, $scope) {
	return $resource('/developer/admin/:itemKey/:fieldKey/versions', {}, {
    });
}]);;function PageCtrl($scope, $http) {
    $http.get('/developer/content').
        success(function(data) {
        	$scope.content = data;
        });
};angular.module('templates-dist', ['app/admin/admin-properties.tpl.html', 'common/security/login/login.tpl.html']);

angular.module("app/admin/admin-properties.tpl.html", []).run(["$templateCache", function($templateCache) {
  "use strict";
  $templateCache.put("app/admin/admin-properties.tpl.html",
    "<div class=\"container\">\n" +
    "				<form class=\"form-horizontal\" role=\"form\" name=\"adminForm\" ng-controller=\"AdminController\" novalidate att-form-errors autocomplete=\"off\">\n" +
    "						<div class=\"form-group\">\n" +
    "							<div class=\"col-sm-offset-2 col-sm-5\">\n" +
    "								<h3>Global Properties</h3>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<div class=\"form-group\">\n" +
    "						    	<div class=\"col-sm-7 col-sm-offset-2\">\n" +
    "						    		<div class=\"has-error help-block\">\n" +
    "					        			<label name=\"top_lvl_errors\" att-field-errors></label>\n" +
    "					        		</div>\n" +
    "									<div class=\"has-success help-block\" ng-show=\"adminProp.successMessage != null && adminForm.$pristine\">\n" +
    "										{{adminProp.successMessage}}\n" +
    "									</div>\n" +
    "					        	</div>\n" +
    "				        </div>\n" +
    "				\n" +
    "						<div class=\"form-group\" ng-class=\"{'has-error' : validateOnSubmit && adminForm.itemKey.$invalid}\">\n" +
    "							<label class=\"required col-sm-2 control-label\" for=\"itemKey_label\">Item Key</label>\n" +
    "							 <div class=\"col-sm-5\">\n" +
    "								<input name=\"itemKey\" type=\"text\" ng-model=\"adminProp.itemKey\" required att-field-errors tabindex=\"1\" typeahead=\"itemKey for itemKey in searchIK($viewValue)\" typeahead-loading=\"loadingLocations\"  class=\"form-control\"/><i ng-show=\"loadingLocations\" class=\"glyphicon glyphicon-refresh\"></i>\n" +
    "							 </div>\n" +
    "						</div>\n" +
    "						<div class=\"form-group\" ng-class=\"{'has-error' : validateOnSubmit && adminForm.fieldKey.$invalid}\">\n" +
    "							<label class=\"required col-sm-2 control-label\" for=\"fieldKey_label\">Field Key</label>\n" +
    "							<div class=\"col-sm-5\">\n" +
    "								<input name=\"fieldKey\" type=\"text\" id=\"fieldKey_input_id\" ng-model=\"adminProp.fieldKey\" required att-field-errors tabindex=\"2\" typeahead=\"itemKey for itemKey in searchFK($viewValue)\" typeahead-loading=\"loadingLocations\"  class=\"form-control\"/><i ng-show=\"loadingLocations\" class=\"glyphicon glyphicon-refresh\"></i>\n" +
    "							</div>	\n" +
    "						</div>\n" +
    "						<div class=\"form-group\" ng-class=\"{'has-error' : validateOnSubmit && adminForm.description.$invalid}\">\n" +
    "							<label class=\"required col-sm-2 control-label\" for=\"description_label\">Description</label>\n" +
    "							<div class=\"col-sm-7\">\n" +
    "								<textarea name=\"description\" id=\"description_textArea_id\" ng-model=\"adminProp.description\" tabindex=\"3\" rows=\"18\" class=\"form-control\"></textarea>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<div class=\"form-group\">\n" +
    "							<div class=\"col-sm-offset-2 col-sm-10\">\n" +
    "								<button data-ng-click=\"validateOnSubmit=true; refresh(adminForm)\" class=\"btn btn-primary\">Refresh</button>\n" +
    "								<button data-ng-click=\"validateOnSubmit=true; create(adminForm)\" class=\"btn btn-primary\">Create</button>\n" +
    "								<button data-ng-click=\"validateOnSubmit=true; update(adminForm)\" class=\"btn btn-primary\">Update</button>\n" +
    "								<button data-ng-click=\"validateOnSubmit=true; delete(adminForm)\" class=\"btn btn-primary\">Delete</button>\n" +
    "							</div>\n" +
    "						</div>\n" +
    "						<br>\n" +
    "						\n" +
    "						<div class=\"form-group\">\n" +
    "							<label class=\"col-sm-2 control-label\">Diff version: </label>\n" +
    "							<select class=\"col-sm-1\" ng-model=\"fromVersion\" ng-options=\"version as version for version in versions\"></select><label class=\"col-sm-1 control-label\">against:</label><select class=\"col-sm-1\" ng-model=\"withVersion\" ng-options=\"version as version for version in versions\" ng-change=\"diff()\"></select>\n" +
    "						</div>\n" +
    "						<div class=\"form-group\">\n" +
    "							<label class=\"col-sm-2 control-label\">Response:</label>\n" +
    "							<pre class=\"col-sm-7\"><div ng-bind-html=\"diff.results\"></div></pre>\n" +
    "						</div>\n" +
    "				</form>\n" +
    "</div>\n" +
    "");
}]);

angular.module("common/security/login/login.tpl.html", []).run(["$templateCache", function($templateCache) {
  "use strict";
  $templateCache.put("common/security/login/login.tpl.html",
    "<div class=\"page-header\">\n" +
    "	<h2>Login</h2>\n" +
    "</div>\n" +
    "\n" +
    "<div class=\"row\">\n" +
    "\n" +
    "	<div class=\"col-md-6\">\n" +
    "		<form class=\"form-horizontal\" name=\"loginForm\" ng-controller=\"LoginController\" novalidate att-form-errors autocomplete=\"off\">\n" +
    "			<div class=\"form-group\">\n" +
    "				<label for=\"username\" class=\"col-sm-3 control-label\">User Name:</label>\n" +
    "				<div class=\"col-sm-9\">\n" +
    "					<input id=\"username\" ng-model=\"login.username\" type=\"text\" class=\"form-control\" />\n" +
    "				</div>\n" +
    "			</div>\n" +
    "			<div class=\"form-group\">\n" +
    "				<label for=\"password\" class=\"col-sm-3 control-label\">Password:</label>\n" +
    "				<div class=\"col-sm-9\">\n" +
    "					<input id=\"password\" ng-model=\"login.password\" type=\"password\" class=\"form-control\" />\n" +
    "				</div>\n" +
    "			</div>\n" +
    "			<div class=\"form-group\">\n" +
    "				<div class=\"col-sm-9 col-sm-offset-3\">\n" +
    "					<input type=\"submit\" value=\"Log In\" class=\"btn btn-primary\" ng-click=\"login(loginForm)\"/>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</form>\n" +
    "	</div>\n" +
    "	\n" +
    "</div>");
}]);
