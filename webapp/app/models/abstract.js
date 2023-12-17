projectApp.service("Abstract", function () {

	var Abstract = function(data) {
		angular.extend(this, data);
	};
	
	return Abstract;

});
