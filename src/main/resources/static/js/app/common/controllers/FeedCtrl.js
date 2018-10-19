'use strict';

angular.module('socialNetworkApp').controller('FeedCtrl',
    function ($rootScope, $location, $scope, $timeout, $state, $interval, $sce, postService, communityService) {

        if ($rootScope.principal === undefined) $location.path('/403');

        var self = this;
        
        self.post = {};
        self.posts = getPosts2();

        self.totalElements = self.posts.totalElements;
        self.currentPage = 1;
        self.itemsPerPage = 5;

        self.count = 0;
        self.somethingNew = false;
        
        self.pageChanged = function () {
            postService.loadUserNews($rootScope.principal.id, self.currentPage, self.itemsPerPage).then(
                function (response) {
                    self.posts = getPosts2();
                    self.count = 0;
                    self.somethingNew = false;
                },
                function (errResponse) {
                }
            );
        };

        var timer = $interval(function () {
            if ($state.current.controller !== "FeedCtrl") {
                $interval.cancel(timer);
                return;
            }
            if (self.currentPage !== 1) return;

            postService.loadUserNews($rootScope.principal.id, 1, 5).then(
                function (response) {
                    var loadedPosts = getPosts2();
                    if (loadedPosts.content.length === 0) return;
                    if (self.posts.content.length === 0 ||  (loadedPosts.content[0].id !== self.posts.content[0].id && loadedPosts.totalElements > self.totalElements)){
                        self.count = loadedPosts.totalElements - self.totalElements;
                        self.somethingNew = true;
                    }
                    self.message = '';
                    self.error = '';
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            );
        }, 5000);

        self.updateNews = function () {
            self.posts = getPosts2();
            self.totalElements += self.count;
            self.somethingNew = false;
            self.count = 0;
        };

        self.createPost = function () {
            self.post.author = $rootScope.principal;
            self.post.ownerId = $rootScope.principal.id;
            postService.createPost(self.post, $scope.attachments).then(
                function (response) {
                    self.post = {};
                    postService.loadUserNews($rootScope.principal.id, 1, 5);
                }
            );

        };

        function getPosts2() {
            return postService.getPosts();
        }

        self.getPosts = function getPosts() {
            return postService.getPosts();
        };

        self.trustSrc = function(src) {
            return $sce.trustAsResourceUrl(src);
        };
    }
);