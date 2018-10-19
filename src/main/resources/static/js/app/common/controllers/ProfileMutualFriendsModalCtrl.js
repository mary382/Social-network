'use strict';

angular.module('socialNetworkApp').controller('ProfileMutualFriendsModalCtrl',
    function ($rootScope, $uibModalInstance, regexPatterns, friendshipService) {
        var self = this;

        self.cancel = cancel;
        self.getUsers = getUsers;

        function cancel() {
            $uibModalInstance.dismiss();
        }

        function getUsers() {
            return friendshipService.getUsers();
        }
    }
);