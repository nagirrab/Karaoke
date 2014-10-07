/**
 * Host routes.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('host.routes', ['karaoke.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/host',  {templateUrl: '/assets/javascripts/host/host.html', controller:controllers.HostCtrl})
      .otherwise( {templateUrl: '/assets/javascripts/host/notFound.html'});
  }]);
  return mod;
});
