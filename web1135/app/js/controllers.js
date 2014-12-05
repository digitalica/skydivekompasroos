'use strict';

/* Controllers */

var kompasroosControllers = angular.module('kompasroosControllers', []);

kompasroosControllers.controller('CanopyListController', ['$scope', 'Canopies',
  function($scope, Canopies) {

    $scope.canopies = Canopies.query();


    $scope.orderProp = 'alpha';
  }]);

kompasroosControllers.controller('CanopyDetailController', ['$scope', '$routeParams',
  function($scope, $routeParams) {
    //$scope.canopy = 
    
    
    
  }]);
