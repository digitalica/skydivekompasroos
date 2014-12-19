'use strict';

/* jasmine specs for controllers go here */
describe('kompasroosServices', function() {

    describe('KompasroosCalculator', function() {

        var calculator;

        var MINAREACAT1 = 170;
        var MINAREACAT2 = 170;
        var MINAREACAT3 = 150;
        var MINAREACAT4 = 135;
        var MINAREACAT5 = 120;

        var MAXWINGLOADCAT1 = 1.1;
        var MAXWINGLOADCAT2 = 1.1;
        var MAXWINGLOADCAT3 = 1.3;
        var MAXWINGLOADCAT4 = 1.5;
        var MAXWINGLOADCAT5 = 1.7;

        beforeEach(module("skydivekompasroosApp"));

        beforeEach(inject(function($injector) {
            calculator = $injector.get('KompasroosCalculator');
        }));

        it('should return correct lbs for kgs', function() {
            expect(calculator.kgToLbs(0)).toEqual(0);
            expect(calculator.kgToLbs(2)).toEqual(4);
            expect(calculator.kgToLbs(200)).toEqual(441);
            expect(calculator.kgToLbs(201)).toEqual(443);
        });

        it('should return correct category based on jumps jumps', function() {
            expect(calculator.jumperCategory(0, 0)).toEqual(1); // absolute beginner
            expect(calculator.jumperCategory(2000, 1000)).toEqual(6); // sky god
            expect(calculator.jumperCategory(2000, 0)).toEqual(6); // jump vacation
            expect(calculator.jumperCategory(999, 0)).toEqual(1); // jump vacation
        });
  
        it('should return correct min area based on category', function() {
            expect(calculator.minAreaBasedOnCategory(1)).toEqual(MINAREACAT1);
            expect(calculator.minAreaBasedOnCategory(2)).toEqual(MINAREACAT2);
            expect(calculator.minAreaBasedOnCategory(3)).toEqual(MINAREACAT3);
            expect(calculator.minAreaBasedOnCategory(4)).toEqual(MINAREACAT4);
            expect(calculator.minAreaBasedOnCategory(5)).toEqual(MINAREACAT5);
        });

        it('should return correct max wingload based on category', function() {
            var precision = 3;
            expect(calculator.maxWingLoadBasedOnCategory(1)).toBeCloseTo(MAXWINGLOADCAT1, precision);
            expect(calculator.maxWingLoadBasedOnCategory(2)).toBeCloseTo(MAXWINGLOADCAT2, precision);
            expect(calculator.maxWingLoadBasedOnCategory(3)).toBeCloseTo(MAXWINGLOADCAT3, precision);
            expect(calculator.maxWingLoadBasedOnCategory(4)).toBeCloseTo(MAXWINGLOADCAT4, precision);
            expect(calculator.maxWingLoadBasedOnCategory(5)).toBeCloseTo(MAXWINGLOADCAT5, precision);
            
        });

        it('should return correct min area for cat and weight (light)', function() {
            var lightweight = 60; 
            expect(calculator.minArea(1, lightweight)).toEqual(MINAREACAT1);
            expect(calculator.minArea(2, lightweight)).toEqual(MINAREACAT2);
            expect(calculator.minArea(3, lightweight)).toEqual(MINAREACAT3);
            expect(calculator.minArea(4, lightweight)).toEqual(MINAREACAT4);
            expect(calculator.minArea(5, lightweight)).toEqual(MINAREACAT5);
        });
        
        it('should return correct min area for cat and weight (heavy)', function() {
            var heavyweight = 120; 
            var MINAREACAT1H = Math.round(calculator.kgToLbs(heavyweight) / MAXWINGLOADCAT1);
            var MINAREACAT2H = Math.round(calculator.kgToLbs(heavyweight) / MAXWINGLOADCAT2);
            var MINAREACAT3H = Math.round(calculator.kgToLbs(heavyweight) / MAXWINGLOADCAT3);
            var MINAREACAT4H = Math.round(calculator.kgToLbs(heavyweight) / MAXWINGLOADCAT4);
            var MINAREACAT5H = Math.round(calculator.kgToLbs(heavyweight) / MAXWINGLOADCAT5);
            
            expect(calculator.minArea(1, heavyweight)).toEqual(MINAREACAT1H);
            expect(calculator.minArea(2, heavyweight)).toEqual(MINAREACAT2H);
            expect(calculator.minArea(3, heavyweight)).toEqual(MINAREACAT3H);
            expect(calculator.minArea(4, heavyweight)).toEqual(MINAREACAT4H);
            expect(calculator.minArea(5, heavyweight)).toEqual(MINAREACAT5H);
        });
         
         
        /***
         * We check the SkyGods separately, as they actually have no minimum area
         * limit..
         */
        it('should return correct min area for skygods', function() {
            expect(calculator.minAreaBasedOnCategory(6)).toEqual(0);
            expect(calculator.minArea(6, 50)).toEqual(0); // light weight
            expect(calculator.minArea(6, 90)).toEqual(0); // medium weight
            expect(calculator.minArea(6, 130)).toEqual(0); // heavy weight
        });
         

    });

});

