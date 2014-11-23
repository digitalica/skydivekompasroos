'use strict';

/* App Module */

var skydivekompasroosApp = angular.module('skydivekompasroosApp', [
  'ngRoute',
  'kompasroosControllers',
  'kompasroosFilters',
  'kompasroosServices'
]);

phonecatApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/phones', {
        templateUrl: 'partials/canopy-list.html',
        controller: 'CanopyListCtrl'
      }).
      when('/phones/:phoneId', {
        templateUrl: 'partials/canopy-detail.html',
        controller: 'CanopyDetailCtrl'
      }).
      otherwise({
        redirectTo: '/canopies'
      });
  }]);
