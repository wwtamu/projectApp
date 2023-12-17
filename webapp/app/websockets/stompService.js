projectApp.service("StompService", function($q, $timeout, AuthService) {

	var StompService = {
		client: null
	};
	
	if(global.stomp) {
		StompService.client = global.stomp.client;
		delete global.stomp;
	}
	
	var refreshingToken = false;
	
	StompService.subscribe = function(channel, persist) {

		if(!persist) persist = false;

		var promiseObj = {
			'subscription': null,
			'channel': channel,
			'defer': $q.defer(),
			'persist': persist
		};

		promiseObj.subscription = StompService.client.subscribe(channel, function(data) {
			promiseObj.defer.notify(data);
		});

		return promiseObj;

	};
		
	StompService.send = function(request, headers, payload) {					
		StompService.client.send(request, headers, payload);		
	};
	

	StompService.subPromiseObj = [];
	
	StompService.request =function(apiReq) {
	
		var id = JSON.stringify({
			'endpoint': apiReq.endpoint,
			'controller': apiReq.controller,
			'method': apiReq.method,
			'persist': apiReq.persist
		});

		var request = '/ws/'+apiReq.controller+'/' + apiReq.method;
		var channel = apiReq.endpoint + "/" + apiReq.controller + "/" + apiReq.method;

		var defer = $q.defer();

		if(!StompService.subPromiseObj[id]) {
			StompService.subPromiseObj[id] = StompService.subscribe(channel, apiReq.persist);
		}

		StompService.subPromiseObj[id].defer.promise.then(null, null, function(data) {

			if(StompService.subPromiseObj[id].persist) {
				defer.notify(data);
			}
			else {
				defer.resolve(data);
			}

		});

		StompService.send(request, {'data':apiReq.data}, {});

		return defer.promise;

	};
	
	
	StompService.reset = function() {
		for(var key in StompService.subPromiseObj) {
			StompService.subPromiseObj[key].subscription.unsubscribe();
		}
		StompService.subPromiseObj = [];
	};
		
	StompService.connect = function() {		
		return $q(function(resolve) {
			StompService.client = Stomp.over(new SockJS(global.restService + '/connect?access_token=' + atob(sessionStorage.access_token))) ;		
			StompService.client.connect({}, function(response) { resolve(); }, function(response) {});
			StompService.client.onclose = function() {
				$timeout(function() { 
					 StompService.stomp.connect({}, function(response) { resolve(); }, function(response) {});
				}, 10000);
			};
		});
	};
	
	return StompService;

});
