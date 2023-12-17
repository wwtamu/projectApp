projectApp.controller('UserController', function($scope, User, StompService) {

	$scope.user = User.get();
	
	$scope.test = function() {
					
		StompService.request({
			endpoint: '/private/queue',
			controller: 'user',
			method: 'test',
			persist: false
		}).then(function(response) {
			console.log(response);
		});
		 
	};
	  	
});
