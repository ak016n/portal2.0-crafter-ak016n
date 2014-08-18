// Karma configuration
// Generated on Wed Jun 04 2014 17:09:36 GMT-0700 (Pacific Daylight Time)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '../../',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
	  'src/main/webapp/resources/script/vendor/angular/*.js',
      'src/main/webapp/resources/script/vendor/angular-resource/*.js',
      'src/main/webapp/resources/script/vendor/angular-translate/*.js',
      'src/main/webapp/resources/script/vendor/angular-translate-loader-url/*.js',
      'src/main/webapp/resources/script/vendor/jquery/*.js',
      'src/main/webapp/resources/script/vendor/angular-mocks/*.js',
      'src/main/webapp/resources/script/vendor/angular-bootstrap/*.js',
      'client/app/*.js',
      'client/app/**/*.js',
      'client/test/unit/**/*.js'
    ],


    // list of files to exclude
    exclude: [
      
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
    
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['Chrome'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,
    
    reporters: ['dots', 'junit'],
    
    junitReporter : {
    	outputFile: 'build/reports/karma/test-results.xml'
    }
   
  });
};
