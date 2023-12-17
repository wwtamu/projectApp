'use strict';

angular.module('projectApp.version', [
	'projectApp.version.interpolate-filter',
	'projectApp.version.version-directive'
])

.value('version', '0.1');

