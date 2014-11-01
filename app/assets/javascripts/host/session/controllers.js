define([], function() {
  'use strict';

  var HostCreateSessionCtrl = function(Session, $scope, $rootScope, $location) {
      $rootScope.pageTitle = 'Create a Session';
      $scope.session = {
        autoOrder: false,
        autoApprove: true,
        userId: 1,
        startDate: new Date,
        status: "OPEN",
        notes: ""
      };

      $scope.submit = function() {
       $scope.$broadcast('show-errors-check-validity');

        if ($scope.sessionForm.$valid) {
          Session.save($scope.session, function(newSession) {
            $location.path("host/session/" + newSession.id + "/settings");
          })
        }

      };
    };
  HostCreateSessionCtrl.$inject = ['Session', '$scope', '$rootScope', '$location'];

  var HostEditSessionCtrl = function(Session, $scope, $rootScope, $location, $routeParams, flash, $window) {
        $rootScope.pageTitle = 'Edit Session Details';
        $scope.sessionId = $routeParams.sessionId;
        $scope.session = Session.get({sessionId: $scope.sessionId});

        $scope.cancel = function() {
          $window.history.back();
        }

        $scope.submit = function() {
         $scope.$broadcast('show-errors-check-validity');

          if ($scope.sessionForm.$valid) {
            Session.save({sessionId: $scope.sessionId}, $scope.session,
            function(successResult) {
              flash.success({
                   text: "Updated Successfully",
                   seconds: 10
                 });
              $location.path("host/session/" + $scope.sessionId);
            },
            function(failureResult) {
              flash.danger({
                text: failureResult,
                seconds: 10
              });
            }
            )
          } else {
            alert("validation error");
          }

        };
      };
  HostEditSessionCtrl.$inject = ['Session', '$scope', '$rootScope', '$location', '$routeParams', 'flash', '$window'];

  var HostSongSessionCtrl = function(Session, $scope, $rootScope, $location, $routeParams, playRoutes, flash) {
      $rootScope.pageTitle = 'Session Details';
      $scope.sessionId = parseInt($routeParams.sessionId);
      $scope.session = Session.get({sessionId: $scope.sessionId});

      playRoutes.controllers.api.host.SessionController.details($scope.sessionId).get().success(function(data) {
        $scope.songQueue = data.activeSongs;
        $scope.songs = _.object(_.map(data.songs, function(s) { return [s.id, s];}));
        $scope.singers = _.object(_.map(data.singers, function(s) { return [s.id, s];}));

      }).error(function(error) {
        flash.danger({
          text: error,
          seconds: 10
        });
      });

      $scope.statusOptions = ["AWAITING_OPEN", "ACCEPTING_REQUESTS", "OPEN", "NO_MORE_REQUESTS", "CLOSED"]

      $scope.updateStatus = function() {
        playRoutes.controllers.api.host.SessionController.update($scope.sessionId).put($scope.session);
      }
  };
    HostSongSessionCtrl.$inject = ['Session', '$scope', '$rootScope', '$location', '$routeParams', 'playRoutes', 'flash'];


  /** Controls the header */
  var HeaderCtrl = function($scope, helper, $location) {
    // Wrap the current user from the service in a watch expression
    $scope.$watch(function() {
      var user = userService.getUser();
      return user;
    }, function(user) {
      $scope.user = user;
    }, true);

    $scope.logout = function() {
      userService.logout();
      $scope.user = undefined;
      $location.path('/');
    };
  };
  HeaderCtrl.$inject = ['$scope', 'helper', '$location'];

  /** Controls the footer */
  var FooterCtrl = function(/*$scope*/) {
  };
  //FooterCtrl.$inject = ['$scope'];

  return {
    HeaderCtrl: HeaderCtrl,
    FooterCtrl: FooterCtrl,
    HostCreateSessionCtrl: HostCreateSessionCtrl,
    HostEditSessionCtrl: HostEditSessionCtrl,
    HostSongSessionCtrl: HostSongSessionCtrl
  };

});
