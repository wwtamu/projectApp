projectApp.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	$locationProvider.html5Mode(true);
	$routeProvider.
		when('/blog', {
			templateUrl: 'views/blog.html'
		}).
		when('/gallary', {
			templateUrl: 'views/gallary.html'
		}).
		when('/profile', {
			templateUrl: 'views/profile.html'
		}).
		when('/settings', {
			templateUrl: 'views/settings.html'
		}).
		when('/admin', {
			templateUrl: 'views/admin.html'
		}).
		otherwise({
			redirectTo: '/dashboard',
			templateUrl: 'views/dashboard.html'
		});
}]);