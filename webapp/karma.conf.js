module.exports = function(config){
	config.set({

		basePath : './',

		files : [      
			'app/bower_components/**/*.js',
			'app/resources/**/*.js',
			'app/components/**/*.js',      
			'app/*.js',
			'tests/**/*.js',
		],

		autoWatch : true,

		frameworks: ['jasmine'],

		browsers : ['Chrome'],

		plugins : [
			'karma-chrome-launcher',
			'karma-firefox-launcher',
			'karma-jasmine',
			'karma-junit-reporter'
		],

		junitReporter : {

			outputFile: 'test_out/unit.xml',

			suite: 'unit'

		}
	});
};


