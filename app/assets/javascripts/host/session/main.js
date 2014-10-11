define(['angular', './routes', './controllers'], function(angular, routes, controllers) {
  'use strict';

  var mod = angular.module('karaoke.host.session', ['ngRoute', 'host.session.routes', 'ui.bootstrap.showErrors', 'flash']);
  mod.controller('HeaderCtrl', controllers.HeaderCtrl);
  mod.controller('FooterCtrl', controllers.FooterCtrl);
  return mod;
});
