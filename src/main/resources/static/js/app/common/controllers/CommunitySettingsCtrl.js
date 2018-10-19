'use strict';

angular.module('socialNetworkApp').controller('CommunitySettingsCtrl',
    function ($rootScope, $location, $stateParams, $uibModal, communityService, regexPatterns) {

        var self = this;

        self.community = undefined;

        self.blacklist = undefined;
        self.blacklistTotal = undefined;
        self.usersPerPage = 10;
        self.currentPage = 1;

        self.message = '';
        self.error = '';
        self.idPattern = regexPatterns.ID;

        self.initCommunity = initCommunity;

        self.blacklistPageChanged = function () {
            var fromIndex = (self.currentPage - 1) * self.usersPerPage;
            var toIndex = ((self.currentPage - 1) * self.usersPerPage + self.usersPerPage > self.community.blockedUsers.length)
                ? self.community.blockedUsers.length
                : (self.currentPage - 1) * self.usersPerPage + self.usersPerPage;
            self.blacklist = self.community.blockedUsers.slice(fromIndex, toIndex);
        };

        self.blockUser = function (userId) {
            communityService.blockUser(self.community.id, parseInt(userId)).then(
                function (response) {
                    self.message = "User with id " + userId + " blocked succesfully";
                    self.blacklistTotal += 1;
                    initCommunity();
                    self.error = '';
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            )
        };

        self.unblockUser = function (userId) {
            communityService.unblockUser(self.community.id, parseInt(userId)).then(
                function (response) {
                    self.message = "User with id " + userId + " unblocked succesfully";
                    self.blacklistTotal -= 1;
                    initCommunity();
                    self.error = '';
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            )
        };

        self.openUpdateModal = function openUpdateModal() {
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/community-modal',
                controller: 'CommunityModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    community: function () {
                        return angular.copy(self.community);
                    },
                    operation: function () {
                        return "Update";
                    }
                }
            });
            modalInstance.result.then(function (community) {
                self.community = community;
            });
        };

        self.openDeleteModal = function openDeleteModal() {
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/community-modal',
                controller: 'CommunityModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    community: function () {
                        return angular.copy(self.community);
                    },
                    operation: function () {
                        return "Delete";
                    }
                }
            });
            modalInstance.result.then(function (community) {
                self.community = community;
                self.totalCommunities -= 1;
            });
        };

        function initCommunity() {
            communityService.getCommunity($stateParams.id).then(
                function (community) {
                    self.community = community.data;
                    if ($rootScope.principal === undefined || $rootScope.principal.id !== self.community.owner.id)
                        $location.path('/403');
                    self.blacklist = (self.community.blockedUsers.length < 10)
                        ? self.community.blockedUsers.slice(0, self.community.blockedUsers.length)
                        : self.community.blockedUsers.slice(0, 10);
                    self.blacklistTotal = self.community.blockedUsers.length;
                },
                function (error) {
                    $location.path('/403');
                }
            );
        }
    }
);