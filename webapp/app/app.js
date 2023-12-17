var projectApp = angular.module('projectApp', [
	'ngRoute',
	'ui.bootstrap',
	'projectApp.version'
]).constant('global', global);

projectApp.boot = function() {

	angular.element("body").fadeIn(250);
	
	angular.bootstrap(document, ['projectApp']);
	
	if(sessionStorage.expired) {
		console.log("SESSION EXPIRED");
		angular.element('#loginModal').modal('show');
		angular.element('#username').val(atob(sessionStorage.id));
		angular.element('#username').trigger('input');
		angular.element('#reauth_message').text("You're session has expired. Please reauthenticate.");
	}
				
};
