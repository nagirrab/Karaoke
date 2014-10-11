/**
 * Common functionality.
 */
define(['angular', './services/helper', './filters', './flash', './directives/example', './services/session'],
    function(angular) {
  'use strict';

  return angular.module('karaoke.common', ['common.helper', 'common.filters',
    'common.directives.example', 'common.session']);
});
