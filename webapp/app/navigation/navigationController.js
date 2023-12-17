projectApp.controller('Navigation', function($scope, $location) {
	
	$scope.$on('$routeChangeStart', function(next, current) {		
		$scope.view = $location.$$path;   
 	});

});
