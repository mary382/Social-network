'use strict';

angular.module('socialNetworkApp').factory('postService',
    function ($localStorage, $q, $http, urls, friendshipService, $rootScope, $stateParams) {

        return {
            createPost: createPost,
            createCommunityPost: createCommunityPost,
            loadAllPosts: loadAllPosts,
            loadAllPostsOfUser: loadAllPostsOfUser,
            newloadAllPostsOfUser: newloadAllPostsOfUser,
            loadAllPostsOfCommunity: loadAllPostsOfCommunity,
            getPosts: getPosts,
            removeCommunityPost: removeCommunityPost,
            removePost: removePost,
            updatePost: updatePost,
            loadUserNews: loadUserNews
        };

        function createPost(post, attachments) {
            var deferred = $q.defer();
            var fd = new FormData();
            for (var i in attachments) {
                fd.append('attachments', attachments[i]);
            }
            $http.post(urls.POST_SERVICE_API, post).then(
                function (response) {
                    var postId = response.data.id;
                    $http.post(urls.POST_SERVICE_API + postId + '/attach', fd, {
                        headers: {'Content-Type': undefined},
                        transformRequest: angular.identity
                    }).then(
                        function (response) {
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                            deferred.reject(errResponse);
                        }
                    );
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function createCommunityPost(post, communityID, attachments) {
            var deferred = $q.defer();
            var fd = new FormData();
            for (var i in attachments) {
                fd.append('attachments', attachments[i]);
            }
            $http.post(urls.COMMUNITY_SERVICE_API + '/' + communityID + '/post', post).then(
                function (response) {
                    var postId = response.data.data.id;
                    $http.post(urls.COMMUNITY_SERVICE_API + '/' + communityID + '/post/attach?postId=' + postId, fd, {
                        headers: {'Content-Type': undefined},
                        transformRequest: angular.identity
                    }).then(
                        function (response) {
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                            deferred.reject(errResponse);
                        }
                    );
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function updatePost(post, attachments) {
            var deferred = $q.defer();
            var fd = new FormData();
            for (var i in attachments) {
                fd.append('attachments', attachments[i]);
            }
            if (post.ownerId < 0) {
                var communityId = post.ownerId * (-1);
                $http.put(urls.COMMUNITY_SERVICE_API + '/' + communityId + '/post?postId=' + post.id, post).then(
                    function (response) {
                        $http.post(urls.COMMUNITY_SERVICE_API + '/' + communityId + '/post/attach?postId=' + post.id, fd, {
                            headers: {'Content-Type': undefined},
                            transformRequest: angular.identity
                        }).then(
                            function (response) {
                                deferred.resolve(response.data);
                            },
                            function (errResponse) {
                                deferred.reject(errResponse);
                            }
                        );
                    },
                    function (errResponse) {
                        deferred.reject(errResponse);
                    }
                );
            }
            else {
                $http.put(urls.POST_SERVICE_API + post.id, post).then(
                    function (response) {
                        $http.post(urls.POST_SERVICE_API + post.id + '/attach', fd, {
                            headers: {'Content-Type': undefined},
                            transformRequest: angular.identity
                        }).then(
                            function (response) {
                                deferred.resolve(response.data);
                            },
                            function (errResponse) {
                                deferred.reject(errResponse);
                            }
                        );
                    },
                    function (errResponse) {
                        deferred.reject(errResponse);
                    }
                );
            }
            return deferred.promise;
        }

        function loadAllPosts() {
            var deferred = $q.defer();
            $http.get(urls.POST_SERVICE_API).then(
                function (response) {
                    $localStorage.posts = response.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadAllPostsOfUser(id) {
            var deferred = $q.defer();
            $http.get(urls.PROFILE_SERVICE_API + id + '/post/').then(
                function (response) {
                    $localStorage.posts = response.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function newloadAllPostsOfUser(id, page, size) {
            var deferred = $q.defer();
            $http.get(urls.PROFILE_SERVICE_API + id + '/post?page=' + page + '&size=' + size).then(
                function (response) {
                    $localStorage.posts = response.data.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadUserNews(id, page, size) {
            var deferred = $q.defer();
            $http.get(urls.PROFILE_SERVICE_API + id + '/news?page=' + page + '&size=' + size).then(
                function (response) {
                    $localStorage.posts = response.data.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadAllPostsOfCommunity(id, page, size) {
            var deferred = $q.defer();
            $http.get(urls.COMMUNITY_SERVICE_API + '/' + id + '/post?page=' + page + '&size=' + size).then(
                function (response) {
                    $localStorage.posts = response.data.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getPosts() {
            return $localStorage.posts;
        }

        function removeCommunityPost(postId, communityId) {
            var deffered = $q.defer();
            $http.delete(urls.COMMUNITY_SERVICE_API + '/' + communityId + "/post?postId=" + postId).then(
                function (response) {
                    deffered.resolve(response.data);
                },
                function (errResponse) {
                    deffered.reject(errResponse);
                }
            );
            return deffered.promise;
        }

        function removePost(id) {
            var deferred = $q.defer();
            $http.delete(urls.POST_SERVICE_API + id).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }
    }
);