'use strict';

angular.module('socialNetworkApp').directive('scroll',
    function ($timeout) {

        return {
            restrict: 'A',
            link: function (scope, element, attr) {
                scope.$watchCollection(attr.scroll, function () {
                    $timeout(function () {
                        element[0].scrollTop = element[0].scrollHeight;
                    });
                });
            }
        }
    }
);