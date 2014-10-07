/**
 * Common functionality.
 */
define(['angular', './services/helper', './filters', './directives/example'],
    function(angular) {
  'use strict';

  return angular.module('karaoke.common', ['common.helper', 'common.filters',
    'common.directives.example']);
});
