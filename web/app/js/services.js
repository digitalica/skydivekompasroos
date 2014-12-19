
'use strict';

/* Services */

var kompasroosServices = angular.module('kompasroosServices', ['ngResource']);

kompasroosServices.factory('KompasroosData', ['$resource', function($resource) {
    var api = {};
    
    api.get = function(cbfunction) {
        var kompasroosdata = {};
        var manufacturersResource = $resource('json/manufacturers.json').get(
            function() {
                kompasroosdata.manufacturers = manufacturersResource.manufacturers;
                kompasroosdata.manufacturersById = [];
                for (var mid in kompasroosdata.manufacturers ) {
                    var manufacturer = kompasroosdata.manufacturers[mid];
                    kompasroosdata.manufacturersById[manufacturer.id] = manufacturer;
                }
                var canopiesResource = $resource('json/canopies.json').get(
                    function() {
                        kompasroosdata.canopiesFromFile = canopiesResource.canopies;
                        kompasroosdata.canopies = [];
                        kompasroosdata.canopiesById = [];
                        for (var cid in kompasroosdata.canopiesFromFile ) {
                            var org = kompasroosdata.canopiesFromFile[cid];
                            var canopy = {
                      "id": org.id,
            "name": org.name,
            "category": parseInt(org.category),
            "cells": parseInt(org.cells),
            "commontype": org.commontype,
            "dropzoneid": org.dropzoneid,
            "firstyearofproduction": org.firstyearofproduction,
            "lastyearofproduction": org.lastyearofproduction,
            "manufacturerid": org.manufacturerid,
            "maxsize": parseInt(org.maxsize),
            "minsize": parseInt(org.minsize),
            "youtube": org.youtube,
            "url": org.url,
            
            // onbekende cat, wordt 6 in berekeningen
            "categoryForCalculations": org.category?org.category:6,
            "categoryForDisplay": org.category?org.category:"?",
            
            
            
                            }
                            canopy.manufacturer = kompasroosdata.manufacturersById[canopy.manufacturerid];
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

        cbfunction(kompasroosdata);
                    }
                )
            }
        );

    }
    
    return api;
}]);


kompasroosServices.factory('KompasroosCalculator',  function($resource) {
    var calculator = {};
    
    /***
     * real constants
     */
    calculator.WEIGHT_FACTOR_KG_TO_LBS = 2.20462262185;
    
    /***
     * settings of categories 1-6
     */
    calculator.MINIMUMTOTALJUMPS = [0, 0, 25, 100, 400, 700, 1000];
    calculator.MINIMUMJUMPSLAST12MONTHS = [ 0, 0, 10, 25, 50, 100, 0];

    /***
     * kind of ENUM
     */
    calculator.ACCEPTABLE = 1;
    calculator.NEEDEDSIZENOTAVAILABLE = 2;
    calculator.CATEGORYTOOHIGH = 3;


	/***
	 * Convert a weight in kg to pounds. The result is rounded.
	 * 
	 * @param kg
	 * @return lbs
	 */
    calculator.kgToLbs = function(kg) {
        return Math.round(kg * this.WEIGHT_FACTOR_KG_TO_LBS);
    };


	/** 
	 * Return the winload based on area and kg
	 * 
	 * @param area
	 * @param weightInKg
	 * @return
	 */
	calculator.wingLoad = function(area, weightInKg) {
		var weightInLbs = this.kgToLbs(weightInKg);
		var wingload = weightInLbs / area;
		return wingload;
	};

	/***
	 * Calculate the jumper category based on the total jumps and jumps in last
	 * month
	 * 
	 * @param totalJumps
	 * @param jumpsLast12Months
	 * @return
	 */
	calculator.jumperCategory = function(totalJumps, jumpsLast12Months) {
		// TODO: below can be done in a simple loop
		var categoryBasedOnTotalJumps = 0;
		if (totalJumps < this.MINIMUMTOTALJUMPS[2])
			categoryBasedOnTotalJumps = 1;
		else if (totalJumps < this.MINIMUMTOTALJUMPS[3])
			categoryBasedOnTotalJumps = 2;
		else if (totalJumps < this.MINIMUMTOTALJUMPS[4])
			categoryBasedOnTotalJumps = 3;
		else if (totalJumps < this.MINIMUMTOTALJUMPS[5])
			categoryBasedOnTotalJumps = 4;
		else if (totalJumps < this.MINIMUMTOTALJUMPS[6])
			categoryBasedOnTotalJumps = 5;
		else
			categoryBasedOnTotalJumps = 6;

		var categoryBasedOnJumpsLast12Months = 0;
		if (jumpsLast12Months < this.MINIMUMJUMPSLAST12MONTHS[2])
			categoryBasedOnJumpsLast12Months = 1;
		else if (jumpsLast12Months < this.MINIMUMJUMPSLAST12MONTHS[3])
			categoryBasedOnJumpsLast12Months = 2;
		else if (jumpsLast12Months < this.MINIMUMJUMPSLAST12MONTHS[4])
			categoryBasedOnJumpsLast12Months = 3;
		else if (jumpsLast12Months < this.MINIMUMJUMPSLAST12MONTHS[5])
			categoryBasedOnJumpsLast12Months = 4;
		else if (jumpsLast12Months < this.MINIMUMJUMPSLAST12MONTHS[6])
			categoryBasedOnJumpsLast12Months = 5;
		else
			categoryBasedOnJumpsLast12Months = 6;

		var jumperCategory;
		if (categoryBasedOnTotalJumps == 6)
			jumperCategory = 6; // if 1000 jumps, no recent exp needed.
		else
			jumperCategory = Math.min(categoryBasedOnTotalJumps,categoryBasedOnJumpsLast12Months);
		return jumperCategory;
	};

	/***
	 * Get the minimal area based on the category note: the special result 0
	 * means there is NO limit
	 * 
	 * @param jumperCategory
	 * @return
	 */
	calculator.minAreaBasedOnCategory = function(jumperCategory) {
		var minAreaBasedOnCategory = 999; // SHOULD NEVER BE RETURNED

		switch (jumperCategory) {
		case 1:
			minAreaBasedOnCategory = 170;
			break;
		case 2:
			minAreaBasedOnCategory = 170;
			break;
		case 3:
			minAreaBasedOnCategory = 150;
			break;
		case 4:
			minAreaBasedOnCategory = 135;
			break;
		case 5:
			minAreaBasedOnCategory = 120;
			break;
		case 6:
			minAreaBasedOnCategory = 0; // NO LIMIT
			break;
		}
		return minAreaBasedOnCategory;
	}

    /***
	 * Get the max wing load based on the category
	 * 
	 * @param jumperCategory
	 * @return
	 */
	calculator.maxWingLoadBasedOnCategory = function(jumperCategory) {
		var maxWingload = 9999;

		switch (jumperCategory) {
		case 1:
			maxWingload = 1.1;
			break;
		case 2:
			maxWingload = 1.1;
			break;
		case 3:
			maxWingload = 1.3;
			break;
		case 4:
			maxWingload = 1.5;
			break;
		case 5:
			maxWingload = 1.7;
			break;
		case 6:
			// no limits
			break;
		}
		return maxWingload;
	};

	/***
	 * Get the minimal area of a canopy, from the category of the jumper and his
	 * weight
	 * 
	 * @param jumperCategory
	 * @param exitWeightInKg
	 * @return
	 */
	calculator.minArea = function(jumperCategory, exitWeightInKg) {
		var maxWingload = this.maxWingLoadBasedOnCategory(jumperCategory);
		var minAreaBasedOnCategory = this.minAreaBasedOnCategory(jumperCategory);
		if (minAreaBasedOnCategory == 0) // means there is NO LIMIT
			return minAreaBasedOnCategory;
		var minAreaBasedOnExitWeight = Math.round(this.kgToLbs(exitWeightInKg) / maxWingload);
		var minArea = Math.max(minAreaBasedOnCategory, minAreaBasedOnExitWeight);
		return minArea;
	};
    
    
    
	/***
	 * Determines if a canopy is acceptable for a given jumper
	 * 
	 * @param jumperCategory
	 * @param exitWeightInKg
	 * @return
	 */
	calculator.acceptablility = function(jumperCategory, exitWeightInKg, canopy) {
		if (parseInt(jumperCategory) < parseInt(canopy.categoryForCalculations))
			return this.CATEGORYTOOHIGH; // not acceptable
		if (canopy.maxSize != "" && canopy.maxSize != null)
			if (parseInt(canopy.maxSize) < parseInt(this.minArea) (jumperCategory, exitWeightInKg)) {
				return this.NEEDEDSIZENOTAVAILABLE;
            }
		return this.ACCEPTABLE;
	}

    
    
    return calculator;
});
