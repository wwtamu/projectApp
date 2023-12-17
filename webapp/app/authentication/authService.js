projectApp.service('AuthService', ["$http", function($http) {
  
	return { 
		refresh: function(id) {		
			var request = $http.get(global.authService + "/refresh",  		 
  				{
  					'headers': {
    					'Accept': 'application/json',
    					'Content-Type': 'application/json',
    					'client-id': sessionStorage.id,
    					'refresh-token': sessionStorage.refresh_token    					 
   					} 
   				}
   			);
		
		 	return request;
		},
		login: function(id) {
			var request = $http.get(global.authService + "/login",  		 
  				{
  					'headers': {
    					'Accept': 'application/json',
    					'Content-Type': 'application/json',
    					'client-id': btoa(id.username),
    					'client-secret': btoa(id.password),
    					'client-remember': (id.remember) ? btoa('true') : btoa('false')
   					} 
   				}
   			);
		
		 	return request;
		},
		logout: function() {
			var request = $http.get(global.authService + "/logout",  		 
  				{
  					'headers': {
    					'Accept': 'application/json',
    					'Content-Type': 'application/json',
    					'client-id': sessionStorage.id,
    					'refresh-token': sessionStorage.refresh_token
   					} 
   				}
   			);
		
		 	return request;
		},
		register: function(application) {
			var request = $http.get(global.authService + "/register",  		 
  				{
  					'headers': {
    					'Accept': 'application/json',
    					'Content-Type': 'application/json',
    					'client-id': btoa(application.username),
    					'client-firstname': btoa(application.firstname),
    					'client-lastname': btoa(application.lastname),
    					'client-email': btoa(application.email),
    					'client-secret': btoa(application.password),
    					'client-confirm': btoa(application.confirm)
   					} 
   				}
   			);
		
		 	return request;
		} 
	};

}]);
