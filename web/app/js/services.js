
'use strict';

/* Services */

var kompasroosServices = angular.module('kompasroosServices', ['ngResource']);

kompasroosServices.factory('KompasroosData', ['$resource', function($resource) {
    var kompasroosdata = {};
    var manufacturers = $resource('json/manufacturers.json').query(
        function() {
            kompasroosdata.manufacturers = manufacturers;
            kompasroosdata.manufacturersById = [];
            for (var mid in kompasroosdata.manufacturers ) {
                var manufacturer = kompasroosdata.manufacturers[mid];
                kompasroosdata.manufacturersById[manufacturer.id] = manufacturer;
            }
            var canopies = $resource('json/canopies.json').query(
                function() {
                    kompasroosdata.canopiesFromFile = canopies;
                    kompasroosdata.canopies = [];
                    kompasroosdata.canopiesById = [];
                    for (var cid in kompasroosdata.canopiesFromFile ) {
                        var org = kompasroosdata.canopiesFromFile[cid];
                        var canopy = {
                  "id": org.id,
        "name": org.name,
        "category": org.category,
        "cells": org.cells,
        "commontype": org.commontype,
        "dropzoneid": org.dropzoneid,
        "firstyearofproduction": org.firstyearofproduction,
        "lastyearofproduction": org.lastyearofproduction,
        "manufacturerid": org.manufacturerid,
        "maxsize": org.maxsize,
        "minsize": org.minsize
                        }
                        canopy.manufacturer = kompasroosdata.manufacturersById[canopy.manufacturerid];
                        canopy.sort_name = canopy.name;
                        canopy.sort_category = String(canopy.category) + canopy.manufacturer.name + canopy.name;
                        canopy.sort_manufacturer = canopy.manufacturer.name + canopy.name;
                        kompasroosdata.canopies.push(canopy);
                        kompasroosdata.canopiesById[canopy.id] = canopy;
                    }


                    // connect manufacturers to canopies
//                    for(var cid in kompasroosdata.canopies) {
//                        var canopy = kompasroosdata.canopies[cid];
//                        var manufacturerId = canopy.manufacturerid + " ";
//                        manufacturerId = manufacturerId.substr(0,manufacturerId.indexOf(' '));
//                        canopy.manufacturer = kompasroosdata.manufacturersById[manufacturerId];
//                    }
                }
            )
        }
    );

    return kompasroosdata;
}]);
