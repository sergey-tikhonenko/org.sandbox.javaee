'use strict';

angular.module('myApp.users', ['ngRoute', 'usersService'])

.config(['$httpProvider', '$routeProvider', function($httpProvider, $routeProvider) {
	/*
	 * Use a HTTP interceptor to add a nonce to every request to prevent MSIE from caching responses.
	 */
	$httpProvider.interceptors.push('ajaxNonceInterceptor');

	$routeProvider
	.when('/users', {
		templateUrl: 'resources/partials/users.tpl.html',
		controller: 'UsersCtrl'
	})
	.when('/user/new', {
		templateUrl: 'resources/partials/user-edit.tpl.html',
		controller: 'UserNewCtrl'
	})
	.when('/user/:userId', {
		templateUrl: 'resources/partials/user-edit.tpl.html',
		controller: 'UserEditCtrl'
	})
	.otherwise({
      redirectTo:'/users'
    });;
}])

// Controller for user list
.controller('UsersCtrl', ['$scope', 'Users', function($scope, Users) {

	// Set the default orderBy to the name property
	$scope.orderBy = ['lastName', 'firstName'];

	// Define a clearMessages function that resets the values of the error and success messages.
	$scope.clearMessages = function () {
		$scope.successMessages = '';
		$scope.errorMessages = '';
	};

	// Define a refresh function, that updates the data from the REST service
	$scope.refresh = function() {
		console.log("refresh");

		// clear messages
		$scope.clearMessages();

		$scope.users = Users.query(function(data) {

			console.log('Model: ' + angular.toJson($scope.users));

		}, function(result) {
			console.log('Error - status:'+ result.status +', response:'+ angular.toJson(result.data));
			if ((result.status == 409) || (result.status == 400)) {
				$scope.errorMessages = result.data;
			} else if ((result.status == 500) || (result.status == 403)) {
				$scope.errorMessages = result.data;
			} else {
				$scope.errorMessages = [ 'Unknown  server error' ];
			}
		});

	};

	$scope.remove = function(userId) {

		var removed = Users.remove({userId:userId}, function(data) {

			console.log('User removed: ' + userId);

			// re-populate the list of users
			$scope.refresh();

		}, function(result) {
			// clear messages
			$scope.clearMessages();

			console.log('Error - status:'+ result.status +', response:'+ angular.toJson(result.data));

			if ((result.status == 409) || (result.status == 400)) {
				$scope.errorMessages = result.data;
			} else if ((result.status == 500) || (result.status == 403)) {
				$scope.errorMessages = result.data;
			} else {
				$scope.errorMessages = [ 'Unknown  server error' ];
			}
		});
	};

	// Call the refresh() function, to populate the list of users
	$scope.refresh();
}])
// Controller for new user form
.controller('UserNewCtrl', ['$scope', '$location', 'Users', function($scope, $location, Users) {
//	$scope.user = {};
	$scope.master = {};

	// Define a clearMessages function that resets the values of the error and success messages.
	$scope.clearMessages = function () {
		$scope.successMessages = '';
		$scope.errorMessages = '';
		$scope.backendErrors = {};
	};

	$scope.update = function() {

		Users.save($scope.user, function(data) {

			$scope.master = angular.copy($scope.user);

			// Clear the form
			$scope.reset();

			// mark success on the registration form
			$scope.successMessages = [ 'Member Registered' ];
			console.log('Member Registered');

			$location.path('/users');

		}, function(result) {
			console.log('Error - status:'+ result.status +', response:'+ angular.toJson(result.data));
			if ((result.status == 409) || (result.status == 400)) {
				$scope.backendErrors = result.data;
			} else if ((result.status == 500) || (result.status == 403)) {
				$scope.errorMessages = result.data;
			} else {
				$scope.errorMessages = [ 'Unknown  server error' ];
			}
		});
	};

	$scope.reset = function() {
		$scope.user = angular.copy($scope.master);
		// clear messages
		$scope.clearMessages();
	};

	$scope.isUnchanged = function(user) {
		return angular.equals(user, $scope.master);
	};

	// Initialise the model
	$scope.reset();
}])
.controller('UserEditCtrl', ['$scope', '$routeParams', '$location', 'Users', function($scope, $routeParams, $location, Users) {
//	$scope.user = {};
	$scope.master = {};
	$scope.userId = $routeParams.userId;

	// Define a clearMessages function that resets the values of the error and success messages.
	$scope.clearMessages = function () {
		$scope.successMessages = '';
		$scope.errorMessages = '';
		$scope.backendErrors = {};
	};

	$scope.update = function() {
		Users.update({userId:$routeParams.userId}, $scope.user, function(data) {

			$scope.master = angular.copy($scope.user);

			// Clear the form
			$scope.reset();

			// mark success on the registration form
			$scope.successMessages = [ 'User updated' ];
			console.log('User updated');

			$location.path('/users');

		}, function(result) {
			if ((result.status == 409) || (result.status == 400)) {
				$scope.backendErrors = result.data;
			} else if ((result.status == 500) || (result.status == 403)) {
				$scope.errorMessages = result.data;
			} else {
				$scope.errorMessages = [ 'Unknown  server error' ];
			}
		});
	};

	$scope.reset = function() {
		// clear messages
		$scope.clearMessages();

		$scope.user = Users.get({userId:$routeParams.userId}, function(data) {

			$scope.master = angular.copy($scope.user);
			console.log('User: ' + angular.toJson($scope.user));
			// clear messages
			$scope.clearMessages();

		}, function(result) {
			console.log('Error - status:'+ result.status +', response:'+ angular.toJson(result.data));
			if ((result.status == 409) || (result.status == 400)) {
				$scope.backendErrors = result.data;
			} else if ((result.status == 500) || (result.status == 403)) {
				$scope.errorMessages = result.data;
			} else {
				$scope.errorMessages = [ 'Unknown  server error' ];
			}
		});
	};

	$scope.isUnchanged = function(user) {
		return angular.equals(user, $scope.master);
	};

	// Initialise the model
	$scope.reset();
}])

.factory('ajaxNonceInterceptor', function() {
	// This interceptor is equivalent to the behavior induced by $.ajaxSetup({cache:false});

	var param_start = /\?/;

	return {

		request : function(config) {
			if (config.method == 'GET') {
				// Add a query parameter named '_' to the URL, with a value equal to the current timestamp
				config.url += (param_start.test(config.url) ? "&" : "?") + '_=' + new Date().getTime();
			}
			return config;
		}
	}
})
;