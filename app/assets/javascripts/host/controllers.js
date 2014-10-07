define([], function() {
  'use strict';

  /** Controls the index page */
  var HostCtrl = function($scope, $rootScope, $location, helper) {
    console.log(helper.sayHi());
    $rootScope.pageTitle = 'Welcome';
  };
  HostCtrl.$inject = ['$scope', '$rootScope', '$location', 'helper'];

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
    HostCtrl: HostCtrl
  };

});
