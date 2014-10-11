/**
 * Host routes.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('host.routes', ['karaoke.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/host/create',  {templateUrl: '/assets/javascripts/host/create.html', controller:controllers.CreateHostCtrl})
      .otherwise( {templateUrl: '/assets/javascripts/host/notFound.html'});
  }]);
  return mod;
});
