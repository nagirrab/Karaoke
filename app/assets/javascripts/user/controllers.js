/**
 * User controllers.
 */
define([], function() {
  'use strict';

  var HomeCtrl = function($scope, $location, userService) {
    $scope.user = userService.getUser();
  };
  HomeCtrl.$inject = ['$scope', '$location', 'userService'];

  var LoginCtrl = function($scope, $location, userService) {
    $scope.credentials = {};

    $scope.login = function(credentials) {
      userService.loginUser(credentials).then(function(/*user*/) {
        $location.path('/home');
      });
    };
  };
  LoginCtrl.$inject = ['$scope', '$location', 'userService'];

  var SignupCtrl = function($scope, $location, userService) {
      $scope.user = {};

      $scope.signUp = function() {
        userService.signUp($scope.user).then(function(/*user*/) {
          $location.path('/host/session/create');
        });
      };
    };
    LoginCtrl.$inject = ['$scope', '$location', 'userService'];

  return {
    HomeCtrl: HomeCtrl,
    LoginCtrl: LoginCtrl,
    SignupCtrl: SignupCtrl
  };

});
