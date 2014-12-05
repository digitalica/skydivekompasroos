
'use strict';

/* Services */

var kompasroosServices = angular.module('kompasroosServices', ['ngResource']);

kompasroosServices.factory('Canopies', ['$resource',
  function($resource){
    return $resource('json/canopies.json');
  }]);


kompasroosServices.factory('Manufacturers', ['$resource',
  function($resource){
    return $resource('json/manufacturers.json');
  }]);
