    module.exports = function(grunt) {
     
      var baseDestination = '../src/main/webapp/resources'; 
    	 
      grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        // Task configuration will be written here
		   bower: {
				install: {
					options: {
						install: true,
						targetDir: baseDestination + '/script/vendor',
						cleanTargetDir: true
					}
				}
			},

			copy: {
				main : {
					files: [
					        {expand: true, cwd: 'app/css', src: ['**'], dest: baseDestination + '/css/ext'},
							{expand: true, cwd: 'dist', src: ['**'], dest: baseDestination + '/script/app/'}
					]
				}
			},
			
			uglify: {
				dist: {
					files: {
						'dist/app.js': [ 'dist/app.js' ]
					},
					options: {
						mangle: false
					}
				}
			},
			
		    html2js: {
		    	options: {
		    		base: 'app/js'
		    	},
				dist: {
					src: [ 'app/**/*.tpl.html' ],
					dest: 'tmp/templates.js'
				}
			},
			
			concat: {
				options: {
					separator: ';'
				},
				dist: {
					src: [ 'app/*.js', 'app/js/**/*.js', 'tmp/*.js' ],
					dest: 'dist/app.js'
				}
			},
			
			jshint: {
				all: [ 'Gruntfile.js', 'app/*.js', 'app/**/*.js' ]
			},
			
		    clean: {
		    	options: { force: true }, // Needed to delete files outside of current directory
				temp: [ 'tmp'],
				build: [baseDestination + '/css/ext', baseDestination + '/script/app']
			},
			
			karma: {
				options: {
					configFile: 'config/dev.karma.conf.js'
				},
				unit: {
					singleRun: true
				},
				continuous: {
					singleRun: false,
					autoWatch: true
				}
			}
      });

      // Loading of tasks and registering tasks will be written here
		grunt.loadNpmTasks('grunt-contrib-jshint');
		grunt.loadNpmTasks('grunt-contrib-clean');
		grunt.loadNpmTasks('grunt-contrib-compress');
		grunt.loadNpmTasks('grunt-contrib-concat');
		grunt.loadNpmTasks('grunt-contrib-uglify');
		grunt.loadNpmTasks('grunt-html2js');
		grunt.loadNpmTasks('grunt-contrib-watch');
		grunt.loadNpmTasks('grunt-bower-task');
		grunt.loadNpmTasks('grunt-karma');
		grunt.loadNpmTasks('grunt-contrib-copy');
		
		//grunt.registerTask('dev', [ 'bower', 'watch:dev' ]);
		grunt.registerTask('test', [ 'bower', 'jshint', 'karma:continuous' ]);
		//grunt.registerTask('minified', [ 'bower', 'connect:server', 'watch:min' ]);
		grunt.registerTask('build', [ 'clean:build', 'bower', 'jshint', 'karma:unit', 'html2js:dist', 'concat:dist', 'uglify:dist', 'copy', 'clean:temp' ]);
    };