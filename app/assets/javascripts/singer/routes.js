/**
 * Host routes.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('singer.routes', ['singer.services', 'karaoke.common']);
  mod.config(['$routeProvider', 'singerResolve', function($routeProvider, singerResolve) {
    $routeProvider
      .when('/singer',  {templateUrl: '/assets/javascripts/singer/singer.html', controller:controllers.SingerCtrl })
      .when('/singer/:sessionId',  {templateUrl: '/assets/javascripts/singer/session.html', controller:controllers.SessionCtrl, resolve: singerResolve})
      .when('/singer/:sessionId/request',  {templateUrl: '/assets/javascripts/singer/request.html', controller:controllers.RequestCtrl, resolve: singerResolve})
      .when('/singer/:sessionId/join',  {templateUrl: '/assets/javascripts/singer/join.html', controller:controllers.JoinSessionCtrl})
      .when('/singer/:sessionId/rejoin',  {templateUrl: '/assets/javascripts/singer/rejoin.html', controller:controllers.RejoinSessionCtrl})
      .otherwise( {templateUrl: '/assets/javascripts/singer/singer.html', controller:controllers.SingerCtrl });
  }]);
  return mod;
});
