define(['angular', './routes', './controllers',  './services'], function(angular, routes, controllers) {
  'use strict';

  var mod = angular.module('karaoke.singer', ['ngRoute', 'singer.routes', 'singer.services']);
  mod.controller('HeaderCtrl', controllers.HeaderCtrl);
  mod.controller('FooterCtrl', controllers.FooterCtrl);
  return mod;
});
