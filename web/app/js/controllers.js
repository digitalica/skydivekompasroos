'use strict';

/* Controllers */

var kompasroosControllers = angular.module('kompasroosControllers', []);

kompasroosControllers.controller('CanopyListController', ['$scope', 'KompasroosData',
  function($scope, KompasroosData) {

    $scope.data = KompasroosData;

    $scope.category = 2;
    $scope.order = 'sort_category';
    $scope.filter = 'filt_close';
    
    $scope.sortkey = function(canopy) {
        var $result = canopy.name;
        switch ($scope.order) {
            case 'sort_name':
                $result = canopy.name;
                break;
            case 'sort_category':
                $result = String(canopy.category) + canopy.manufacturer.name + canopy.name;
                break;
            case 'sort_manufacturer':
                $result = canopy.manufacturer.name + canopy.name;
                break;
        }
        return $result;
    };
    
    
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
