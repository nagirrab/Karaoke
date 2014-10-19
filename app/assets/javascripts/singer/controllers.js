define([], function() {
  'use strict';

  /** Controls the index page */
  var SingerCtrl = function($scope, $rootScope, $location, helper) {
    console.log(helper.sayHi());
    $rootScope.pageTitle = 'Welcome';
  };
  SingerCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper'];

  var SessionCtrl = function($scope, $rootScope, $location, helper, playRoutes) {
      console.log(helper.sayHi());
      $rootScope.pageTitle = 'Session Details';

      $scope.addRequest = function() {
        $location.path($location.path() + "/request");
      }

      playRoutes.controllers.api.singer.SessionSongController.activeSongs().get().then(function(data) {
        $scope.activeSongs = data.data;
      })
    };
    SessionCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper', 'playRoutes'];

  var RequestCtrl = function($scope, $rootScope, $location, helper, playRoutes) {
    console.log(helper.sayHi());
    $rootScope.pageTitle = 'Request A Song';
    $scope.request = {

    };

    $scope.submitRequest = function() {
      var result = playRoutes.controllers.api.singer.SessionSongController.requestSong().post($scope.request);

      result.success(function(data) {
        alert("success");
      }).error(function(data) {
        alert("failure")
      });
    }

  };
  RequestCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper', 'playRoutes'];

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
    RequestCtrl: RequestCtrl
  };

});
