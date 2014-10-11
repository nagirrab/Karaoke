define(['angular', './routes', './controllers', 'host/session'], function(angular, routes, controllers) {
  'use strict';

  var mod = angular.module('karaoke.host', ['ngRoute', 'host.routes', 'karaoke.host.session']);
  mod.controller('HeaderCtrl', controllers.HeaderCtrl);
  mod.controller('FooterCtrl', controllers.FooterCtrl);
  return mod;
});
