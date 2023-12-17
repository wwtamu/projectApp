projectApp.service("User", function(Abstract) {

	var self;

	var User = function(data) {
		self = this;
		angular.extend(self, Abstract);
		angular.extend(self, data);
		
		if(global.user) {
			angular.extend(self, global.user);
			delete global.user;
		}
	};
	
	User.data = null;
	
	User.get = function() {
	
		if(User.data) return User.data;
		
		User.data = new User();
		
		return User.data;
	}
	
	User.set = function(data) {
		angular.extend(self, data);
	}
		
	return User;
	
});

