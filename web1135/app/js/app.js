'use strict';

/* App Module */

var skydivekompasroosApp = angular.module('skydivekompasroosApp', [
  'ngRoute',
  'kompasroosControllers',
//  'kompasroosFilters',
  'kompasroosServices'
]);

skydivekompasroosApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/canopy-list', {
        templateUrl: 'partials/canopy-list.html',
        controller: 'CanopyListController'
      }).
      when('/canopy-detail/:phoneId', {
        templateUrl: 'partials/canopy-detail.html',
        controller: 'CanopyDetailController'
      }).
      otherwise({
        redirectTo: '/canopy-list'
      });
  }]);
