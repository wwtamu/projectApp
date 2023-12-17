'use strict';

angular.module('projectApp.version.version-directive', [])

.directive('appVersion', ['version', function(version) {
	return function(scope, elm, attrs) {
		elm.text(version);
	};
}]);

