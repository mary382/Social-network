'use strict';

angular.module('socialNetworkApp').controller('ProfileFollowersModalCtrl',
    function ($rootScope, $uibModalInstance, regexPatterns, friendshipService, id) {
        var self = this;

        self.cancel = cancel;
        self.getUsers = getUsers;
        self.getId = getId;


        function cancel() {
            $uibModalInstance.dismiss();
        }

        function getUsers() {
            return friendshipService.getUsers();
        }

        function getId() {
            return id;
        }
    }
);