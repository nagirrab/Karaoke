define(['angular'], function(angular) {
  'use strict';

  var mod = angular.module('common.session', ['ngResource']);
  mod.factory('Session', ['$resource', function($resource) {
    return $resource('api/host/sessions/:sessionId/:facet', { sessionId: "@sessionId" },
    { songs: {method:'GET', isArray: true, params:{facet:'songs'}}});
  }]);
  return mod;
});