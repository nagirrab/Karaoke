define(['angular', 'host', 'singer', 'user'], function(angular) {
  'use strict';

  // We must already declare most dependencies here (except for common), or the submodules' routes
  // will not be resolved
  return angular.module('app', ['ui.bootstrap.tpls', 'ui.bootstrap', 'karaoke.host', 'karaoke.singer', 'karaoke.user']);
});