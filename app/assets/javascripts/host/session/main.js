define(['angular', './routes', './controllers'], function(angular, routes, controllers) {
  'use strict';

  var mod = angular.module('karaoke.host', ['ngRoute', 'host.routes']);
  mod.controller('HeaderCtrl', controllers.HeaderCtrl);
  mod.controller('FooterCtrl', controllers.FooterCtrl);
  return mod;
});
