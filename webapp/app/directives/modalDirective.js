projectApp.directive('modal', function($controller) {
	return {
		templateUrl: 'views/modalWrapper.html',
		restrict: 'E',
		replace:false,
		transclude: true,
		scope: false,
		link: function ($scope, element, attr) {	    	
			$scope.attr = attr;
			if($scope.attr.modalController) {
				angular.extend(this, $controller($scope.attr.modalController, {$scope: $scope}));
			}			
	    	$scope.click = function() {
	    		if($scope.attr.modalNgClickFunction && $scope.attr.modalNgClickParam) {
	    			$scope[$scope.attr.modalNgClickFunction](JSON.parse($scope.attr.modalNgClickParam));
	    		}
	    	}
	    }
	};
});

