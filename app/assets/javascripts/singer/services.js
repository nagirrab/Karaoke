/**
 * User service, exposes user model to the rest of the app.
 */
define(['angular', 'common'], function (angular) {
  'use strict';

  var mod = angular.module('singer.services', ['karaoke.common', 'ngCookies']);
  mod.factory('singerService', ['$http', '$q', 'playRoutes', '$cookies', '$log', 'flash', '$location', function ($http, $q, playRoutes, $cookies, $log, flash, $location) {
    var token = $cookies['SINGER_ID'];
    var singer;
    var singerPromise;

    /* If the token is assigned, check that the token is still valid on the server */
    if (token && singer === undefined) {
      $log.info('Restoring singer from cookie...');
      singerPromise = playRoutes.controllers.api.singer.SessionSingerController.currentSinger().get();
      singerPromise.success(function (data) {
          $log.info('Welcome back, ' + data.name);
          singer = data;
          $cookies['SINGER_ID'] = singer.id;
          token = singer.id;
        })
        .error(function () {
          $log.info('Token no longer valid, please log in.');
          token = undefined;
          delete $cookies['SINGER_ID'];
          return $q.reject("Token invalid");
        });
    }

    return {
      loginSinger: function (credentials) {
        return playRoutes.controllers.api.SingerController.login().post(credentials).then(function (response) {
          singer = response.data;
          $cookies['SINGER_ID'] = singer.id;
          token = singer.id;
          return token;
        });
      },
      join: function (joinAttempt) {
        return playRoutes.controllers.api.singer.SessionSingerController.join().post(joinAttempt).success(function (response) {
          $cookies['SINGER_ID'] = response.id;
          token = response.id;
          singer = response;
          $log.info("Created " + singer.name);
          $location.path('/singer/' + response.sessionId);

        }).error(function (response) {
          flash.danger({
             text: response,
             seconds: 10
           });
        });
      },
      rejoin: function (rejoinAttempt) {
        return playRoutes.controllers.api.singer.SessionSingerController.rejoin().post(rejoinAttempt).success(function (response) {
          $cookies['SINGER_ID'] = response.id;
          token = response.id;
          singer = response;
          $log.info("Created " + singer.name);
          $location.path('/singer/' + response.sessionId);
        }).error(function (response) {
        flash.danger({
          text: response,
          seconds: 10
         })
        });
      },
      getSinger: function () {
        if(singer) {
          return singer;
        } else if (singerPromise) {
          return singerPromise;
        }
      }
    };
  }]);
  /**
   * Add this object to a route definition to only allow resolving the route if the singer is
   * logged in. This also adds the contents of the objects as a dependency of the controller.
   */
  mod.constant('singerResolve', {
    singer: ['$q', 'singerService', function ($q, singerService) {
      var deferred = $q.defer();
      var singer = singerService.getSinger();
      if (singer) {
        deferred.resolve(singer);
      } else {
        deferred.reject();
      }
      return deferred.promise;
    }]
  });
  /**
   * If the current route does not resolve, go back to the start page.
   */
  var handleRouteError = function ($rootScope, $location) {
    $rootScope.$on('$routeChangeError', function (/*e, next, current*/) {
      $location.path('/');
    });
  };
  handleRouteError.$inject = ['$rootScope', '$location'];
  mod.run(handleRouteError);
  return mod;
});
