'use strict';

/* Controllers */

var kompasroosControllers = angular.module('kompasroosControllers', []);

kompasroosControllers.controller('CanopyListController', ['$scope', 'KompasroosData', 'KompasroosCalculator',
  function($scope, KompasroosData, KompasroosCalculator) {

    $scope.calculator = KompasroosCalculator;
    $scope.api = KompasroosData;
    $scope.api.get(function(data) {
        $scope.data = data;
    }); 

    $scope.angularversion = angular.version;

    $scope.jumps = 300;
    $scope.jumpslastyear = 25;
    $scope.weight = 80;
    $scope.category = 2;
    $scope.order = 'sort_category';
    $scope.filter = 'filt_close';
    
    
    $scope.discontinued=function(canopy) {
        if (!canopy.lastyearofproduction) {
            return false; // we don't know...
        } else {
            return true; // set, so must be discontinued
        }
    }
    
    // sortkey geeft de juiste sortering terug, afhankelijk van de gekozen volgorder
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

    // canopyFilter geeft true of false, afhankelijk van of deze koepel getoond moet worden
    $scope.canopyFilter = function(canopy) {
        switch($scope.filter) {
            case 'filt_close':
                if (canopy.commontype != 1) {
                    return false;
                }
                if (parseInt(canopy.category) < parseInt($scope.category-1)) {
                    return false;
                }
                if (parseInt(canopy.category) > parseInt($scope.category+1)) {
                    return false;
                }
                return true;
                break;
            case 'filt_common':
                return canopy.commontype == 1;
                break;
            case 'filt_all':
                return true;
                break;
        }
    };


    $scope.canopyAcceptability = function(canopy) {
        var acceptability = $scope.calculator.acceptablility($scope.category, $scope.weight, canopy);
        return acceptability;
    }
    
  }]);

kompasroosControllers.controller('CanopyDetailController', ['$scope', '$routeParams', 'KompasroosData',
  function($scope, $routeParams, KompasroosData) {

//    $scope.data = KompasroosData;

    $scope.api = KompasroosData;
    $scope.api.get(function(data) {
//        $scope.canopy = data.canopiesById[$routeParams.canopyId];
        $scope.data = data;
    if (data && data.canopiesById) {
        $scope.canopy = data.canopiesById[$routeParams.canopyId];
    } else {
        $scope.canopy = {};
    }

        
        
        
    }); 


    // sortkey geeft de juiste sortering terug: op cat en dan naam
    $scope.sortkey = function(canopy) {
        return String(canopy.category) + canopy.manufacturer.name + canopy.name;
    };

    // canopyFilter geeft true of false, afhankelijk van of deze koepel getoond moet worden
    $scope.canopyFilter = function(canopy) {
        if (canopy.manufacturer !== $scope.canopy.manufacturer) {
            return false;
        }
        if (canopy.id === $scope.canopy.id) {
            return false;
        }
        return true;
    };

    
    $scope.discontinued=function(canopy) {
        if (!canopy.lastyearofproduction) {
            return false; // we don't know...
        } else {
            return true; // set, so must be discontinued
        }
    }
    
  }]);
