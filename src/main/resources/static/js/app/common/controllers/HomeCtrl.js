'use strict';

angular.module('socialNetworkApp').controller('HomeCtrl',
    function ($rootScope, $http, $state) {

        var self = this;

        self.check = function () {
            if ($rootScope.authenticated) {
                $state.go('news');
            }
        };
    }
);