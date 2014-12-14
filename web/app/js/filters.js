'use strict';

/* Filters */

angular.module('kompasroosFilters', []).filter('iframeyoutube', function($sce) {
  return function(input) {
      var iframe = '<iframe ';
      iframe += 'id="ytplayer" ';
      iframe += 'type="text/html" ';
      iframe += 'width="640" ';
      iframe += 'height="390" ';
      iframe += 'src="http://www.youtube.com/embed/' + input + '?autoplay=0&origin=http://example.com" ';
      iframe += 'frameborder="0"/>';
      return $sce.trustAsHtml(iframe);
  };
});

