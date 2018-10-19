'use strict';

angular.module('socialNetworkApp').controller('CommunityCtrl',
    function ($rootScope, $scope, $location, $sce, $stateParams, $uibModal, $uibModalStack, communityService, postService,
              fileService) {

        var self = this;

        self.community = undefined;
        self.commParticipantFlag = undefined;
        $scope.logotype = undefined;
        $scope.attachments = [];

        self.posts = getPosts();
        self.newPost = {};
        self.totalPosts = (self.posts === undefined) ? 0 : self.posts.totalElements;
        self.postsPerPage = 5;

        self.participants = undefined;
        self.totalParticipants = undefined;
        self.participantsPerPage = 10;

        self.currentPage = 1;
        self.message = '';
        self.error = '';

        self.getPosts = getPosts;
        self.initCommunity = initCommunity;

        function initCommunity() {
            communityService.getCommunity($stateParams.id).then(
                function (community) {
                    self.community = community.data;
                    self.commParticipantFlag = isCommParticipant();
                    self.participants =  (self.community.participants.length < 10)
                        ? self.community.participants.slice(0, self.community.participants.length)
                        : self.community.participants.slice(0, 10);
                    self.totalParticipants = self.community.participants.length;
                },
                function (error) {
                    $location.path("/403");
                }
            );
        }


        self.uploadLogotype = function () {
            fileService.uploadCommunityLogotype($stateParams.id, $scope.logotype).then(
                function (response) {
                    initCommunity();
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };

        function isCommParticipant() {
            if ($rootScope.principal === undefined) return undefined;
            var flag = false;
            self.community.participants.forEach(function (item) {
                if (item.id === $rootScope.principal.id) {
                    flag = true;
                }
            });
            return flag;
        }

        self.followCommunity = function () {
            communityService.followCommunity($stateParams.id).then(
                function (response) {
                    initCommunity();
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };

        self.unfollowCommunity = function () {
            communityService.unfollowCommunity($stateParams.id).then(
                function (response) {
                    initCommunity();
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };

        self.postPageChanged = function () {
            postService.loadAllPostsOfCommunity($stateParams.id, self.currentPage, self.postsPerPage).then(
                function (response) {
                    self.posts = getPosts();
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };

        self.participantPageChanged = function pageChanged() {
            var fromIndex = (self.currentPage - 1) * self.itemsPerPage;
            var toIndex = ((self.currentPage - 1) * self.itemsPerPage + self.itemsPerPage > community.participants.length)
                ? community.participants.length
                : (self.currentPage - 1) * self.itemsPerPage + self.itemsPerPage;
            self.participants = community.participants.slice(fromIndex, toIndex);
        };

        self.openParticipantsModal = function openParticipantsModal() {
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/participants-modal',
                controller: 'CommunityCtrl',
                controllerAs: 'ctrl'
            });
        };

        function getPosts() {
            return postService.getPosts();
        }

        self.createPost = function createPost() {
            postService.createCommunityPost(self.newPost, $stateParams.id, $scope.attachments).then(
                function (response) {
                    self.newPost = {};
                    postService.loadAllPostsOfCommunity($stateParams.id, 1, self.postsPerPage).then(
                        function (response) {
                            self.posts = getPosts();
                            self.totalPosts += 1;
                        },
                        function (errResponse) {
                            self.error = errResponse.data.error;
                            self.message = '';
                        }
                    );
                }
            );
        };
        self.deletePost = function deletePost(postId) {
            postService.removeCommunityPost(postId, $stateParams.id).then(
                function (response) {
                    postService.loadAllPostsOfCommunity($stateParams.id, 1, self.postsPerPage).then(
                        function (response) {
                            self.currentPage = 1;
                            self.posts = getPosts();
                            self.totalPosts -= 1;
                        },
                        function (errResponse) {
                            self.error = errResponse.data.error;
                            self.message = '';
                        }
                    );
                }
            );
        };

        self.openUpdatePostModal = function openUpdatePostModal(post) {
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/post-edit',
                controller: 'PostEditModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    post: post
                }
            });
            modalInstance.result.then(function () {
                postService.loadAllPostsOfCommunity($stateParams.id, self.currentPage, self.postsPerPage).then(
                    function (response) {
                        self.posts = getPosts();
                    },
                    function (errResponse) {
                        self.error = errResponse.data.error;
                        self.message = '';
                    }
                );
            });
        };

        self.trustSrc = function (src) {
            return $sce.trustAsResourceUrl(src);
        };

        self.cancel  = function cancel() {
            $uibModalStack.dismissAll();
        }
    }
);