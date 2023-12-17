projectApp.controller('Authentication', function($scope, $location, AuthService, StompService, User) {

	$scope.application;
	
	$scope.id;
	
	$scope.rememberMe = false;
	
	$scope.toggleRememberMe = function() {
		$scope.rememberMe = !$scope.rememberMe;
	}
	
	$scope.isAuthenticated = function() {
		return (sessionStorage.authenticated == btoa('true'));
	};
	
	$scope.isAdmin = function() {
		return (sessionStorage.role == btoa('ROLE_ADMIN'));
	}
		
	$scope.login = function(id) {
				
		AuthService.login(id).then(
	  		function(response) {
	  		
	  			console.log(response);
	  			
	  			$scope.id = {};
	  			
	  			delete sessionStorage.expired;
	  			
				sessionStorage.access_token = btoa(response.data.content.DefaultOAuth2AccessToken.access_token);
				sessionStorage.refresh_token = btoa(response.data.content.DefaultOAuth2AccessToken.refresh_token);
				sessionStorage.id = btoa(response.data.content.DefaultOAuth2AccessToken.credentials.id);				
				sessionStorage.role = btoa(response.data.content.DefaultOAuth2AccessToken.credentials.role);
				
				sessionStorage.authenticated = btoa(true);
								
				User.set(response.data.content.DefaultOAuth2AccessToken.credentials);

				StompService.connect().then(function() { });
								
				if($scope.rememberMe) {
					localStorage.id =  btoa(id.username);
					localStorage.refresh_token = sessionStorage.refresh_token;
				}
				
				angular.element('#reauth_message').text("");
				
	  		},
			function(response) {
	  		},
			function(response) {
	  		}
	  	);
	
	};
	
	$scope.logout = function() {
		
		AuthService.logout().then(
			function(response) {
			
				console.log(response);
				
				delete localStorage.id;
				delete localStorage.refresh_token;
				delete localStorage.remember;
				
				delete sessionStorage.access_token;
				delete sessionStorage.refresh_token;
				delete sessionStorage.id;
				delete sessionStorage.role;
								
				delete sessionStorage.authenticated;
								
				User.set(null);
																				
			},
			function(response) {
			},
			function(response) {
		});
		
	};
	
	$scope.register = function(application) {
	
		AuthService.register(application).then(
	  		function(response) {
	  		
	  			console.log(response);
	  		
	  			$scope.application = {};
												
				sessionStorage.access_token = btoa(response.data.content.DefaultOAuth2AccessToken.access_token);
				sessionStorage.refresh_token = btoa(response.data.content.DefaultOAuth2AccessToken.refresh_token);
				sessionStorage.id = btoa(response.data.content.DefaultOAuth2AccessToken.credentials.id);
				sessionStorage.role = btoa(response.data.content.DefaultOAuth2AccessToken.credentials.role);
				
				sessionStorage.authenticated = btoa(true);
								
				User.set(response.data.content.DefaultOAuth2AccessToken.credentials);
				
				StompService.connect().then(function() {});
								
	  		},
			function(response) {				
	  		},
			function(response) {
	  		}
	  	);
	
	};
			  	
});
