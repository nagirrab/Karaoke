define([], function() {
  'use strict';

  /** Controls the index page */
  var SingerCtrl = function($scope, $rootScope, $location, helper, playRoutes) {
    console.log(helper.sayHi());
    $rootScope.pageTitle = 'Welcome';

    playRoutes.controllers.api.host.SessionController.active().get().success(function (data) {
      $scope.sessions = data;
    })
  };
  SingerCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper', 'playRoutes'];

  var SessionCtrl = function($scope, $rootScope, $location, helper, playRoutes, flash, $interval) {
      console.log(helper.sayHi());
      $rootScope.pageTitle = 'Session Details';

      $scope.addRequest = function() {
        $location.path($location.path() + "/request");
      }

      $scope.cancelSong = function(songId) {
        var result = playRoutes.controllers.api.singer.SessionSongController.cancel(songId).post();

        result.success(function(data) {
          flash.success({
             text: "Song Cancelled",
             seconds: 10
           });
           playRoutes.controllers.api.singer.SessionSongController.activeSongs().get().then(function(data) {
             $scope.activeSongs = data.data;
           })

           playRoutes.controllers.api.singer.SessionSongController.completedSongs().get().then(function(data) {
              $scope.completedSongs = data.data;
            })

        }).error(function(data) {
          flash.danger({
             text: data,
             seconds: 10
           });
        });

      }

      $scope.refreshSession = function() {
        playRoutes.controllers.api.singer.SessionSongController.activeSongs().get().then(function(data) {
          $scope.activeSongs = data.data;
          $scope.onDeck = $scope.activeSongs
        });

        playRoutes.controllers.api.singer.SessionSongController.activeSongs().get().then(function(data) {
          $scope.activeSongs = data.data;
          $scope.onDeck = _.findWhere($scope.activeSongs, { status: "ON_DECK" });
        });
      }

      $scope.refreshSession();

      $interval($scope.refreshSession, 20000)

      playRoutes.controllers.api.singer.SessionSongController.completedSongs().get().then(function(data) {
        $scope.completedSongs = data.data;
      })
    };
    SessionCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper', 'playRoutes', 'flash', '$interval'];

  var RequestCtrl = function($scope, $rootScope, $location, helper, playRoutes, flash) {
    console.log(helper.sayHi());
    $rootScope.pageTitle = 'Request A Song';
    $scope.request = {

    };

    $scope.manualMode = false;

    $scope.doSearch = function(text) {
      return playRoutes.controllers.api.common.SongController.search(text).get().then(function(response) {
        $scope.fullSearchResults = response.data.map(function(s) { s.summary = s.title + " - " + s.artist; return s });
        return _.pluck($scope.fullSearchResults, "summary");
      });
    }

    $scope.unsetSong = function() {
      $scope.request.songSummary = undefined;
      $scope.request.songId = undefined;
    }

    $scope.$watch('request.songSummary', function(newValue, oldValue) {
      var updatedSong = _.findWhere($scope.fullSearchResults, { summary: newValue })
      if (updatedSong === undefined) {
        $scope.request.songId = undefined;
      } else {
        $scope.request.songId = updatedSong.id;
        $scope.request.artist = updatedSong.artist;
        $scope.request.title = updatedSong.title;
      }
    })

    $scope.submitRequest = function() {
      var result = playRoutes.controllers.api.singer.SessionSongController.requestSong().post($scope.request);

      result.success(function(data) {
        flash.success({
           text: "Song Request Received",
           seconds: 10
         });
        $location.path('/singer/' + $scope.sessionId);
      }).error(function(data) {
        flash.danger({
           text: data,
           seconds: 10
         });
      });
    }
  };
  RequestCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper', 'playRoutes', 'flash'];

  var GuestCtrl = function($scope, $rootScope, $location, helper, playRoutes, flash, $routeParams) {
      console.log(helper.sayHi());
      $rootScope.pageTitle = 'Request A Song';

      $scope.sessionId = parseInt($routeParams.sessionId);

      $scope.reset = function() {
        $scope.request = {
          sessionId: $scope.sessionId
        };
      }

      $scope.reset();

      $scope.manualMode = false;

      $scope.doSearch = function(text) {
          return playRoutes.controllers.api.common.SongController.search(text).get().then(function(response) {
          $scope.fullSearchResults = response.data.map(function(s) { s.summary = s.title + " - " + s.artist; return s });
          return _.pluck($scope.fullSearchResults, "summary");
        });
      }

      $scope.unsetSong = function() {
        $scope.request.songSummary = undefined;
        $scope.request.songId = undefined;
      }

      $scope.$watch('request.songSummary', function(newValue, oldValue) {
        var updatedSong = _.findWhere($scope.fullSearchResults, { summary: newValue })
        if (updatedSong === undefined) {
          $scope.request.songId = undefined;
        } else {
          $scope.request.songId = updatedSong.id;
          $scope.request.artist = updatedSong.artist;
          $scope.request.title = updatedSong.title;
        }
      })

      $scope.submitRequest = function() {
        var result = playRoutes.controllers.api.singer.SessionSongController.guestRequestSong().post($scope.request);

        result.success(function(data) {
          flash.success({
             text: "Song Request Received",
             seconds: 10
           });
          $scope.reset();
        }).error(function(data) {
          flash.danger({
             text: data,
             seconds: 10
           });
        });
      }
    };
    GuestCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper', 'playRoutes', 'flash', '$routeParams'];

  var JoinSessionCtrl = function($scope, $rootScope, $location, helper, $routeParams, Session, singerService) {
    $rootScope.pageTitle = 'Join Session';
    $scope.sessionId = parseInt($routeParams.sessionId);
    $scope.session = Session.get({sessionId: $scope.sessionId});

    $scope.joinReq = {
      sessionId: $scope.sessionId
    }

    $scope.join = function() {
      singerService.join($scope.joinReq);
    }
  };
  JoinSessionCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper', '$routeParams', 'Session', 'singerService'];

  var RejoinSessionCtrl = function($scope, $rootScope, $location, helper, $routeParams, Session, singerService) {
    $rootScope.pageTitle = 'Rejoin Session';
    $scope.sessionId = parseInt($routeParams.sessionId);
    $scope.session = Session.get({sessionId: $scope.sessionId});

    $scope.rejoinReq = {
      sessionId: $scope.sessionId
    }

    $scope.rejoin = function() {
      singerService.rejoin($scope.rejoinReq);
    }
  };
  RejoinSessionCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper', '$routeParams', 'Session', 'singerService'];


  /** Controls the header */
  var HeaderCtrl = function($scope, helper, $location, userService, singerService) {
    $scope.$watch(function() {
      var singer = singerService.getSinger();
      return singer;
     }, function(singer) {
       $scope.user = singer;
     }, true);

    $scope.logout = function() {
      singerService.logout();
      $scope.singer = undefined;
      $location.path('/');
    };
  };
  HeaderCtrl.$inject = ['$scope', 'helper', '$location', 'userService', 'singerService'];

  /** Controls the footer */
  var FooterCtrl = function(/*$scope*/) {
  };
  //FooterCtrl.$inject = ['$scope'];

  return {
    HeaderCtrl: HeaderCtrl,
    FooterCtrl: FooterCtrl,
    SingerCtrl: SingerCtrl,
    SessionCtrl: SessionCtrl,
    JoinSessionCtrl: JoinSessionCtrl,
    RejoinSessionCtrl: RejoinSessionCtrl,
    RequestCtrl: RequestCtrl,
    GuestCtrl: GuestCtrl
  };

});
