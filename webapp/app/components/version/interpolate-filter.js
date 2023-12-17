'use strict';

angular.module('projectApp.version.interpolate-filter', [])

.filter('interpolate', ['version', function(version) {
	return function(text) {
		return String(text).replace(/\%VERSION\%/mg, version);
	};
}]);


