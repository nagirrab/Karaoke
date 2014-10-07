/**
 * Host routes.
 */
define(['angular', './controllers', 'common'], function(angular, controllers) {
  'use strict';

  var mod = angular.module('singer.routes', ['karaoke.common']);
  mod.config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/singer',  {templateUrl: '/assets/javascripts/singer/singer.html', controller:controllers.SingerCtrl})
      .otherwise( {templateUrl: '/assets/javascripts/singer/notFound.html'});
  }]);
  return mod;
});
