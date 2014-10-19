define(['angular'], function(angular) {
  'use strict';

  var mod = angular.module('common.session', ['ngResource']);
  mod.factory('User', ['$resource', function($resource) {
    return $resource('api/user/:sessionId/:facet', { sessionId: "@sessionId" },
    { songs: {method:'GET', isArray: true, params:{facet:'songs'}}});
  }]);
  return mod;
});