function start(bootstrap) {
	
	angular.element(document).ready(function() {
			
		bootstrap();
		
	});
	
}

function setup(bootstrap) {
	
	console.log("Bootstrapping App");
		
	if(sessionStorage.refresh_token || (localStorage.refresh_token && localStorage.id)) {
		
		console.log("With token");
		
		if(!sessionStorage.refresh_token) {
			sessionStorage.refresh_token = localStorage.refresh_token;
			sessionStorage.id = localStorage.id;
		}
		
		var projectAppInjector = angular.injector(['projectApp']);
								
		var $authService = projectAppInjector.get('AuthService');
		
		var $user = projectAppInjector.get('User');
						
		$authService.refresh().then(
			function(response) {
		
				console.log(response);
										
				sessionStorage.access_token = btoa(response.data.content.DefaultOAuth2AccessToken.access_token);
				sessionStorage.refresh_token = btoa(response.data.content.DefaultOAuth2AccessToken.refresh_token);
				sessionStorage.id = btoa(response.data.content.DefaultOAuth2AccessToken.credentials.id);
				sessionStorage.role = btoa(response.data.content.DefaultOAuth2AccessToken.credentials.role);
				
				sessionStorage.authenticated = btoa('true');
								
				global.stomp = {
					'client': Stomp.over(new SockJS(global.restService + '/connect?access_token=' + atob(sessionStorage.access_token)))
				}
								
				global.stomp.client.connect({}, function(response) {}, function(response) {});
								
				global.user = $user.get();
				
				$user.set(response.data.content.DefaultOAuth2AccessToken.credentials);
								
				start(bootstrap);
							
			},
			function(response) {
				
				console.log(response);
							
				sessionStorage.expired = btoa('true');
				
				delete sessionStorage.access_token;
				delete sessionStorage.refresh_token;				
				delete sessionStorage.role;
				
				delete sessionStorage.authenticated;

				start(bootstrap);
				
			},
			function(response) {
			
				console.log(response);
				
				start(bootstrap);
			
		});
		
		
	} else {

		console.log("Without token");
		
		start(bootstrap);
		
	} 

}
