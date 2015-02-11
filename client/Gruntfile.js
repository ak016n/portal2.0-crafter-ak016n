    module.exports = function(grunt) {
     
      var baseDestination = '../server/src/main/webapp'; 
    	 
      grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

		   bower: {
				install: {
					options: {
						install: true,
						targetDir: baseDestination + '/resources/script/vendor',
						cleanTargetDir: true
					}
				}
			},

			copy: {
				main : {
					files: [
					        {expand: true, cwd: 'src/css', src: ['**'], dest: baseDestination + '/resources/css/ext'},
							{expand: true, cwd: 'dist', src: ['**'], dest: baseDestination + '/resources/script/app/'},
							{expand: true, cwd: 'src/app', src: ['index.html'], dest: baseDestination}
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
		    		base: 'src',
		    		useStrict: true
		    	},
				dist: {
					src: [ 'src/**/*.tpl.html' ],
					dest: 'tmp/templates.js'
				}
			},
			
			concat: {
				options: {
					separator: ';'
				},
				dist: {
					src: [ 'src/app/app.js', 'src/common/**/*.js', 'src/app/**/*.js', 'tmp/*.js' ],
					dest: 'dist/app.js'
				}
			},
			
			jshint: {
				all: [ 'Gruntfile.js', 'src/app.js', 'src/**/*.js' ]
			},
			
		    clean: {
		    	options: { force: true }, // Needed to delete files outside of current directory
				temp: [ 'tmp'],
				build: [baseDestination + '/css/ext', baseDestination + '/resources/script/app']
			},
			
			karma: {
				options: {
					configFile: 'dev.karma.conf.js'
				},
				unit: {
					singleRun: true
				},
				continuous: {
					singleRun: false,
					autoWatch: true
				},
				build: {
					singleRun: true,
					browsers: ['PhantomJS']
				}
			},
			
			watch: {
				  scripts: {
				    files: ['**/*.js','**/*.html'],
				    tasks: ['debug'],
				    options: {
				      spawn: false,
				    },
				  },
			}
      });

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
		grunt.registerTask('package', [ 'clean:build', 'bower', 'jshint', 'html2js:dist', 'concat:dist', 'uglify:dist', 'karma:build', 'copy', 'clean:temp' ]);
		grunt.registerTask('debug', [ 'clean:build', 'bower', 'html2js:dist', 'concat:dist', 'copy', 'clean:temp' ]);
		
		// Part of gradle tasks - Splitting to be part of appropriate gradle java build lifecycle
		grunt.registerTask('build', [ 'clean:build', 'bower', 'jshint', 'karma:build']);
		grunt.registerTask('war', [ 'clean:build', 'bower', 'html2js:dist', 'concat:dist', 'uglify:dist', 'copy', 'clean:temp' ]);
    };