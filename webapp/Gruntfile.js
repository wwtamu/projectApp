module.exports = function(grunt) {

	var build = {
		app: 'app',
		dist: 'dist'
	};

	grunt.initConfig({

		build: build,

		useminPrepare: {
			html: '<%= build.app %>/index.html'
		},

		jshint: {
			options: {
				jshintrc: '.jshintrc'				
			},
			all: [
				'<%= build.app %>/**/*.js',
				'!<%= build.app %>/bower_components/**/*',
				'!<%= build.app %>/components/**/*',
				'!<%= build.app %>/resources/**/*'
			]
		},

		concat: {			
			options: {
				separator: ';'
			},			
			angular: {
				src: [					
					'<%= build.app %>/**/*.js',
					'!<%= build.app %>/bower_components/**/*',
					'!<%= build.app %>/components/**/*',
					'!<%= build.app %>/resources/**/*',
				],
				dest: '<%= build.app %>/resources/scripts/app_concat.js'
			}
		},

		uglify: {
			options: {
				mangle: false
			},
			angular: {
				src:  '<%= build.app %>/resources/scripts/app_concat.js',
				dest: '<%= build.app %>/resources/scripts/app_concat.js'
			}
		},

		usemin: {
			html: '<%= build.app %>/index.html',
				options: {
					assetsDirs: ['<%= build.app %>/resources/scripts']
				}
			},
	
		compass: {
			dist: {
				options: {
					sassDir: 'sass',
					cssDir: '<%= build.app %>/resources/styles'
				}
			}
		},

		watch: {
			css: {
				files: '**/*.scss',
				tasks: ['compass']
			}
		}

	});

	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-usemin');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-compass');
	grunt.loadNpmTasks('grunt-contrib-watch');

	grunt.registerTask('default', ['jshint', 'watch']);

	grunt.registerTask('develop', ['jshint', 'useminPrepare', 'concat', 'usemin', 'watch']);
	
	grunt.registerTask('deploy', ['jshint', 'useminPrepare', 'concat', 'uglify', 'usemin', 'compass']);

};

