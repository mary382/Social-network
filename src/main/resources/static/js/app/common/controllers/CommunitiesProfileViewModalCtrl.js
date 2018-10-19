'use strict';

angular.module('socialNetworkApp').controller('CommunitiesProfileViewModalCtrl',
    function ($rootScope, $uibModalInstance, regexPatterns, communityService, id) {
        var self = this;

        self.cancel = cancel;
        self.getCommunities = getCommunities;

        function cancel() {
            $uibModalInstance.dismiss();
        }

        function getCommunities() {
            return communityService.getAllCommunity();
        }
    }
);