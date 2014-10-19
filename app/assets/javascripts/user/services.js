/**
 * User service, exposes user model to the rest of the app.
 */
define(['angular', 'common'], function (angular) {
  'use strict';

  var mod = angular.module('user.services', ['karaoke.common', 'ngCookies']);
  mod.factory('userService', ['$http', '$q', 'playRoutes', '$cookies', '$log', function ($http, $q, playRoutes, $cookies, $log) {
    var token = $cookies['USER_ID'];
    var user;

    /* If the token is assigned, check that the token is still valid on the server */
    if (token && user === undefined) {
      $log.info('Restoring user from cookie...');
      playRoutes.controllers.api.UserController.currentUser().get()
        .success(function (data) {
          $log.info('Welcome back, ' + data.name);
          user = data;
          $cookies['USER_ID'] = user.id;
          token = user.id;
        })
        .error(function () {
          $log.info('Token no longer valid, please log in.');
          token = undefined;
          delete $cookies['USER_ID'];
          return $q.reject("Token invalid");
        });
    }

    return {
      loginUser: function (credentials) {
        return playRoutes.controllers.api.UserController.login().post(credentials).then(function (response) {
          user = response.data;
          $cookies['USER_ID'] = user.id;
          token = user.id;
          return token;
        });
      },
      logout: function () {
        // Logout on server in a real app
        delete $cookies['USER_ID'];
        token = undefined;
        user = undefined;
        return playRoutes.controllers.api.UserController.logout().get().then(function () {
          $log.info("Good bye ");
        });
      },
      signUp: function (user) {
        return playRoutes.controllers.api.UserController.create().post(user).then(function (user) {
          $cookies['USER_ID'] = user.id;
          token = user.id;
          user = user.id;
          $log.info("Created " + user.name);
        });
      },
      getUser: function () {
        return user;
      }
    };
  }]);
  /**
   * Add this object to a route definition to only allow resolving the route if the user is
   * logged in. This also adds the contents of the objects as a dependency of the controller.
   */
  mod.constant('userResolve', {
    user: ['$q', 'userService', function ($q, userService) {
      var deferred = $q.defer();
      var user = userService.getUser();
      if (user) {
        deferred.resolve(user);
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
    debugger
      $location.path('/');
    });
  };
  handleRouteError.$inject = ['$rootScope', '$location'];
  mod.run(handleRouteError);
  return mod;
});
