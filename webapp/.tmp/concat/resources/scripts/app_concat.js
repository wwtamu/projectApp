var global = {};
;var projectApp = angular.module('projectApp', 
	[
		'ngRoute',
		'ngResource',
		'projectApp.version'

	]).constant('global', global);

projectApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	$locationProvider.html5Mode(true);
	$routeProvider.
		when('/profile', {
			templateUrl: 'view/profile.html'
		}).
		when('/settings', {
			templateUrl: 'view/settings.html'
		}).
		otherwise({redirectTo: '/dashboard',
			templateUrl: 'view/dashboard.html'
		});
}]);

projectApp.run(function() {
	document.body.style.display = 'block';
});

angular.element(document).ready(function() {
	angular.bootstrap(document, ["projectApp"]);
});

