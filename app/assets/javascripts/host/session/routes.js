/**
 * Host routes.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('host.session.routes', ['karaoke.common']);
  mod.config(['$routeProvider', function($routeProvider) {

    $routeProvider
      .when('/host/session/create',  {templateUrl: '/assets/javascripts/host/session/create.html', controller:controllers.HostCreateSessionCtrl})
      .when('/host/session/:sessionId/settings',  {templateUrl: '/assets/javascripts/host/session/settings.html', controller:controllers.HostEditSessionCtrl});
  }]);
  return mod;
});
