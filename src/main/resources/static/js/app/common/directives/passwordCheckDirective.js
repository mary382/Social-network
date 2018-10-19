'use strict';

angular.module('socialNetworkApp').directive('passwordConfirm',
    function () {

        return {
            restrict: 'A',
            scope: {
                matchTarget: '='
            },
            require: 'ngModel',
            link: function link(scope, elem, attrs, ctrl) {
                var validator = function (value) {
                    ctrl.$setValidity('mismatch', value === scope.matchTarget);
                    return value;
                };
                ctrl.$parsers.unshift(validator);
                ctrl.$formatters.push(validator);
                scope.$watch('matchTarget', function () {
                    validator(ctrl.$viewValue);
                })
            }
        }
    }
);