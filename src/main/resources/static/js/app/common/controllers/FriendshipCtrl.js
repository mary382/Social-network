'use strict';

angular.module('socialNetworkApp').controller('FriendshipCtrl',
    function ($rootScope, $scope, $stateParams, $uibModal, friendshipService) {

        var self = this;

        self.users = [];
        self.status = 0;
        self.statusMessage = '';
        self.blacklist = {};

        self.getStatusMessage = function getStatusMessage() {
            return friendshipService.getStatusMessage();
        };

        self.message = '';
        self.error = '';

        self.getFriendshipStatus = function getFriendshipStatus() {
            return friendshipService.getStatus();
        };
        self.addFriend = function addFriend(user) {
            friendshipService.addFriend(user.id).then(
                function (response) {
                    if ($stateParams.id !== undefined && $rootScope.principal.id !== $stateParams.id) {
                        friendshipService.loadFriendshipStatus($rootScope.principal.id, $stateParams.id);
                    }
                    else {
                        friendshipService.remove(user);
                    }
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };
        self.deleteFriend = function deleteFriend(user) {
            friendshipService.deleteFriend(user.id).then(
                function (response) {
                    if ($stateParams.id !== undefined && $rootScope.principal.id !== $stateParams.id) {
                        friendshipService.loadFriendshipStatus($rootScope.principal.id, $stateParams.id);
                    }
                    else {
                        friendshipService.remove(user);
                    }
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };

        self.approveAllIncomingRequest = function approveAllIncomingRequest() {
            friendshipService.approveAllIncomingRequest().then(
                function (response) {
                    friendshipService.loadIncomingRequests($rootScope.principal.id);
                    friendshipService.getUsers();
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };
        self.declineAllIncomingRequest = function declineAllIncomingRequest() {
            friendshipService.declineAllIncomingRequest().then(
                function (response) {
                    friendshipService.loadIncomingRequests($rootScope.principal.id);
                    friendshipService.getUsers();
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };
        self.declineAllOutgoingRequest = function declineAllOutgoingRequest() {
            return friendshipService.declineAllOutgoingRequest().then(
                function (response) {
                    friendshipService.loadOutgoingRequests();
                    friendshipService.getUsers();
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };
        self.unblockUser = function unblockUser(user) {
            friendshipService.unblockUser(user.id).then(
                function (response) {
                    if ($stateParams.id !== undefined && $rootScope.principal.id !== $stateParams.id) {
                        friendshipService.loadFriendshipStatus($rootScope.principal.id, $stateParams.id);
                    }
                    else {
                        friendshipService.loadBlacklist();
                        friendshipService.getBlacklist();
                    }
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };
        self.getUsers = function getUsers() {
            return friendshipService.getUsers();
        };
        self.getBlacklist = function getBlacklist() {
            return friendshipService.getBlacklist();
        };

        /* flag = 1 if need to load blacklist,
        flag = 0 in any other situation*/
        self.openBlockUserModal = function openBlockUserModal(user, flag) {
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/block-user-modal',
                controller: 'BlockUserModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    user: user,
                    flag: flag
                }
            });
        };

        self.tabSelect = function tabSelect(index) {
            switch (index) {
                case 0:
                    friendshipService.loadFriends($rootScope.principal.id);
                    break;
                case 1:
                    friendshipService.loadIncomingRequests($rootScope.principal.id);
                    break;
                case 2:
                    friendshipService.loadOutgoingRequests();
                    break;
                case 3:
                    friendshipService.loadBlacklist();
                    break;
            }
        }
    }
);
