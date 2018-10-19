'use strict';

angular.module('socialNetworkApp').controller('ProfileCtrl',
    function ($rootScope, $scope, $stateParams, $location, $uibModal, profileService, postService,
              conversationService, communityService, friendshipService, fileService, $localStorage) {

        var self = this;

        self.profile = {};
        self.newPost = {};
        self.posts = getPosts();
        self.totalPosts = (self.posts === undefined) ? 0 : self.posts.totalElements;
        self.postsPerPage = 5;
        self.currentPage = 1;
        self.communities = [];
        $scope.attachments = [];

        self.openEditModal = openEditModal;
        self.initProfile = initProfile;
        self.getAllProfiles = getAllProfiles;
        self.createConversation = createConversation;
        self.createPost = createPost;
        self.getPosts = getPosts;
        self.getCommunities = getCommunities;
        self.openUpdatePostModal = openUpdatePostModal;
        self.deletePost = deletePost;
        self.openFriendsProfileViewModal = openFriendsProfileViewModal;
        self.openCommunitiesProfileViewModal = openCommunitiesProfileViewModal;
        self.openProfileFollowersModal = openProfileFollowersModal;
        self.openMutualFriendsProfileViewModal = openMutualFriendsProfileViewModal;

        self.postPageChanged = function () {
            postService.newloadAllPostsOfUser($stateParams.id, self.currentPage, self.postsPerPage).then(
                function (response) {
                    self.posts = getPosts();
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };

        function openEditModal() {
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/profile-edit',
                controller: 'ProfileEditModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    profile: function () {
                        return angular.copy(self.profile);
                    }
                }
            });
            modalInstance.result.then(function (profile) {
                self.profile = profile;
                $rootScope.principal = profile;
            });
        }

        function initProfile() {
            profileService.getProfile($stateParams.id).then(
                function (profile) {
                    self.profile = profile;
                    if ($rootScope.principal !== undefined && $rootScope.principal.id !== $stateParams.id) {
                        friendshipService.loadFriendshipStatus($rootScope.principal.id, $stateParams.id);
                    }
                    else {
                        $localStorage.status = -1;
                        friendshipService.getStatus();
                    }
                },
                function () {
                    $location.path('/404');
                }
            );
        }

        function getAllProfiles() {
            return profileService.getAllProfiles();
        }

        function createConversation() {
            var conversation = {};
            conversation.participants = [
                $rootScope.principal,
                self.profile
            ];
            conversationService.createConversation(conversation).then(
                function () {
                    $location.path('/conversations');
                }
            );

        }

        function createPost() {
            self.newPost.author = $rootScope.principal;
            self.newPost.ownerId = $stateParams.id;
            postService.createPost(self.newPost, $scope.attachments).then(
                function (response) {
                    self.newPost = {};
                    postService.newloadAllPostsOfUser($stateParams.id, 1, self.postsPerPage).then(
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
        }

        self.uploadAvatar = function () {
            fileService.uploadAvatar($stateParams.id, $scope.avatar).then(
                function (response) {
                    initProfile();
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        };

        function getPosts() {
            return postService.getPosts();
        }

        function getCommunities() {
            return communityService.getAllCommunity();
        }

        function openUpdatePostModal(post) {
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/post-edit',
                controller: 'PostEditModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    post: post
                }
            });
            modalInstance.result.then(function () {
                postService.newloadAllPostsOfUser($stateParams.id, self.currentPage, self.postsPerPage).then(
                    function (response) {
                        self.posts = getPosts();
                    },
                    function (errResponse) {
                        self.error = errResponse.data.error;
                        self.message = '';
                    }
                );
            });
        }

        function deletePost(id) {
            postService.removePost(id).then(
                function (response) {
                    postService.newloadAllPostsOfUser($stateParams.id, 1, self.postsPerPage).then(
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
        }


        function openFriendsProfileViewModal(id) {
            friendshipService.loadFriends(id);
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/friends-profile-view',
                controller: 'ProfileFriendsModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    id: id
                }
            });
        }

        function openCommunitiesProfileViewModal(id) {
            communityService.loadAllCommunitiesOfUser(id, null, null);
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/communities-profile-view',
                controller: 'CommunitiesProfileViewModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    id: id
                }
            });
        }

        function openProfileFollowersModal(id) {
            friendshipService.loadIncomingRequests(id);
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/followers-profile-view',
                controller: 'ProfileFollowersModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    id: id
                }
            });
        }

        function openMutualFriendsProfileViewModal(userId, friendId) {
            friendshipService.loadMutualFriends(userId, friendId);
            var modalInstance = $uibModal.open({
                templateUrl: 'partials/mutual-friends-profile-view',
                controller: 'ProfileMutualFriendsModalCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    userId: userId,
                    friendId: friendId
                }
            });
        }

    }
);