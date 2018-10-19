'use strict';

angular.module('socialNetworkApp').controller('CommunityListCtrl',
    function ($stateParams, $uibModal, flag, communityService) {

        var self = this;

        self.communities = getCommunities();
        self.totalCommunities = (self.communities === undefined) ? 0 : self.communities.totalElements;
        self.communitiesPerPage = 20;
        self.currentPage = 1;

        self.flag = flag; // true - user communities, false - all communities
        self.message = '';
        self.error = '';

        self.getCommunities = getCommunities;

        self.communityPageChanged = function () {
            if (flag) {
                communityService.loadAllCommunitiesOfUser($stateParams.id, self.currentPage, self.communitiesPerPage).then(
                    function (response) {
                        self.communities = getCommunities();
                    },
                    function (errResponse) {
                        self.error = errResponse.data.error;
                        self.message = '';
                    }
                );
            }
            else {
                communityService.loadAllCommunities(self.currentPage, self.communitiesPerPage).then(
                    function (response) {
                        self.communities = getCommunities();
                    },
                    function (errResponse) {
                        self.error = errResponse.data.error;
                        self.message = '';
                    }
                );
            }
        };

        self.openCreateModal = function openCreateModal() {
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/community-modal',
                controller: 'CommunityModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    community: function () {
                        return angular.copy({});
                    },
                    operation: function () {
                        return "Create";
                    }
                }
            });
            modalInstance.result.then(function (community) {
                self.community = community;
                communityService.loadAllCommunities(1, 20).then(
                    function (response) {
                        self.communities = getCommunities();
                        self.totalCommunities += 1;
                    },
                    function (errResponse) {
                        self.error = errResponse.data.error;
                        self.message = '';
                    }
                );
            });
        };


        function getCommunities() {
            return communityService.getAllCommunity();
        }

    }
);