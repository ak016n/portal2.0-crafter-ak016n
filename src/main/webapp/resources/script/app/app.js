var app = angular.module('portalApp', ['adminCtrl.adminService', 'pascalprecht.translate', 'ui.bootstrap']);

app.config(function($translateProvider) {
	$translateProvider.useUrlLoader('i18N');
	$translateProvider.preferredLanguage('en');
	$translateProvider.fallbackLanguage('en');
});


angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null 
}

app.directive('contentItem', ['$http', '$compile', function ($http, $compile) {

    var getTemplate = function(url) {
        var template = '';
        var finalUrl = '/developer/views/partials/' + url;
        $http.get(finalUrl).then(function(templateHtml) {
        	template = templateHtml.data;
        });
        return template;
    }

    var linker = function(scope, element, attrs) {
        element.html(getTemplate(scope.content.templateUrl)).show;

        $compile(element.contents())(scope);
    }

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

/** Framework directives are added here */
app.directive('attFormErrors', ['$log', 'globalHandleErrorService', function ($log, globalHandleErrorService) {
    return {
        restrict: "A",
        replace: false,
        controller: ['$scope', '$element', function($scope, $element) {
        	var ctrls = {};
        	var formLevelErrorsId = 'top_lvl_errors';
        	
        	this.addCtrl = function(fieldName, ctrl) {
        		ctrls[fieldName] = ctrl;
        	}
        	
        	this.addError = function(fieldName, error) {
        		if (!(fieldName in ctrls)) return;
                return ctrls[fieldName].addError(fieldName, error);
        	}
        	
        	this.clearErrorsFor = function(fieldName) {
        	    if (!(fieldName in ctrls)) return;
        	    return ctrls[fieldName].clearErrors();
        	};

        	this.clearFormLevelErrors = function() {
        		this.clearErrorsFor(formLevelErrorsId);
        	}
        	
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


app.directive('attFieldErrors', ['$log', '$compile', function ($log, $compile) {
	
	var errors = {
	}
	
	var getTemplate = function() {
		return '<label class="control-label" ng-repeat="(key, value) in errorMap" ng-show="validateOnSubmit">{{value | translate}}</label>';
	}
	
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
            }
            
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
        	
        	if(element.parent().find('label').length == 0) {
        		element.parent().parent().find('label').after(compiled);
        	} else {
        		element.parent().find('label').after(compiled);
        	}
        	
        	var clearFormLevelErrors = function() {
        		attFormErrorsCtrl.clearFormLevelErrors();
        	}
        	
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
}]);

app.factory('globalHandleErrorService', ['$translate', '$log', function($translate, $log) {
	  var formColl = [];

	  // Used to set server side errors
	  var globalHandleErrorService = function(opts) {
	    var fieldErrors = opts.fieldErrors;
	    var ctrl = formColl[opts.formName];

	    if(angular.isUndefinedOrNull(fieldErrors)) {
	    	return;
	    }
	    
	    Object.keys(fieldErrors).forEach(function(index) {
	      $log.info('translated fieldErrorKey : ' + $translate.instant(fieldErrors[index]["id"]) + ' fieldErrors : ' + $translate.instant(fieldErrors[index]["message"]));
	      ctrl.addError($translate.instant(fieldErrors[index]["id"]), $translate.instant(fieldErrors[index]["message"]));
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
