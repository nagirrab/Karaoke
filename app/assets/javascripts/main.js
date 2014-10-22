// `main.js` is the file that sbt-web will use as an entry point
(function (requirejs) {
  'use strict';

  // -- RequireJS config --
  requirejs.config({
    // Packages = top-level folders; loads a contained file named 'main.js"
    packages: ['common', 'host', 'host/session', 'singer', 'user'],
    shim: {
      // Hopefully this all will not be necessary but can be fetched from WebJars in the future
      'angular': {
        deps: ['jquery'],
        exports: 'angular'
      },
      'jsRoutes': {
        deps: [],
        // it's not a RequireJS module, so we have to tell it what var is returned
        exports: 'jsRoutes'
      },
      'angular-route': ['angular'],
      'angular-cookies': ['angular'],
      'angular-resource': ['angular'],
      'bootstrap': ['jquery'],
      'angular-showErrors': ['angular'],
      'ui-bootstrap': ['angular', 'bootstrap'],
      'ui-bootstrap-tpls': ['bootstrap']
    },
    paths: {
      'requirejs': ['../lib/requirejs/require'],
      'jquery': ['../lib/jquery/jquery'],
      'angular': ['../lib/angularjs/angular'],
      'angular-route': ['../lib/angularjs/angular-route'],
      'angular-cookies': ['../lib/angularjs/angular-cookies'],
      'angular-resource': ['../lib/angularjs/angular-resource'],
      'angular-showErrors': ['/assets/js/vendor/showErrors'],
      'bootstrap': ['../lib/bootstrap/js/bootstrap'],
      'ui-bootstrap': ['../lib/angular-ui-bootstrap/ui-bootstrap'],
      'ui-bootstrap-tpls': ['../lib/angular-ui-bootstrap/ui-bootstrap-tpls'],
      'jsRoutes': ['/jsroutes'],
    }
  });

  requirejs.onError = function (err) {
    console.log(err);
  };

  // Load the app. This is kept minimal so it doesn't need much updating.
  require(['angular', 'angular-cookies', 'angular-route', 'angular-resource', 'angular-showErrors', 'jquery', 'bootstrap', 'ui-bootstrap-tpls', 'ui-bootstrap', './app'],
    function (angular) {
      angular.bootstrap(document, ['app']);
    }
  );
})(requirejs);
