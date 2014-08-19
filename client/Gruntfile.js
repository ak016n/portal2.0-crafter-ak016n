    module.exports = function(grunt) {
     
      grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        // Task configuration will be written here
		    bower: {
				install: {
					options: {
						install: true,
						targetDir: '../src/main/webapp/resources/script/vendor',
						cleanTargetDir: true
					}
				}
			},

			copy: {
				main : {
					files: [
					        {expand: true, cwd: 'app/css', src: ['**'], dest: '../src/main/webapp/resources/css/ext'},
							{expand: true, src: ['dist/app.js'], dest: '../src/main/webapp/script/app/'}
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
				dist: {
					src: [ 'app/partials/**/*.html' ],
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
				temp: {
					src: [ 'tmp' ]
				}
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
		
		grunt.registerTask('dev', [ 'bower', 'watch:dev' ]);
		grunt.registerTask('test', [ 'bower', 'jshint', 'karma:continuous' ]);
		grunt.registerTask('minified', [ 'bower', 'connect:server', 'watch:min' ]);
		grunt.registerTask('package', [ 'bower', 'jshint', 'karma:unit', 'html2js:dist', 'concat:dist', 'uglify:dist', 'copy', 'clean:temp' ]);
    };