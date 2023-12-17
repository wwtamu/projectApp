projectApp.directive('user', function() {
	return {
		template: '{{user.first_name}} {{user.last_name}}',
		restrict: 'E',
		scope: true,
		controller: 'UserController',		
	};
});

