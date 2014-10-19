/**
 * Configure routes of user module.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('user.routes', ['user.services', 'karaoke.common']);
  mod.config(['$routeProvider', 'userResolve', function($routeProvider, userResolve) {
    $routeProvider
      .when('/home', {templateUrl:'/assets/javascripts/user/home.html', controller:controllers.HomeCtrl, resolve:userResolve})
      .when('/login', {templateUrl:'/assets/javascripts/user/login.html', controller:controllers.LoginCtrl})
      .when('/signup', {templateUrl:'/assets/javascripts/user/create.html', controller:controllers.SignupCtrl});
      //.when('/users', {templateUrl:'/assets/templates/user/users.html', controller:controllers.UserCtrl})
      //.when('/users/:id', {templateUrl:'/assets/templates/user/editUser.html', controller:controllers.UserCtrl});
  }]);
  return mod;
});
