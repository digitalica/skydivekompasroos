'use strict';

/* Controllers */

var kompasroosControllers = angular.module('kompasroosControllers', []);

kompasroosControllers.controller('CanopyListController', ['$scope', 'KompasroosData',
  function($scope, KompasroosData) {

    $scope.data = KompasroosData;

    $scope.orderProp = 'sort_category';
  }]);

kompasroosControllers.controller('CanopyDetailController', ['$scope', '$routeParams', 'KompasroosData',
  function($scope, $routeParams, KompasroosData) {

    $scope.data = KompasroosData;
    if ($scope.data && $scope.data.canopiesById) {
        $scope.canopy = $scope.data.canopiesById[$routeParams.canopyId];
    } else {
        $scope.canopy = {};
    }
    
    
    
  }]);
