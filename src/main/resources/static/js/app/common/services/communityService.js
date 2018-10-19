'use strict';

angular.module('socialNetworkApp').factory('communityService',
    function ($localStorage, $q, $http, urls) {

        return {
            createCommunity: createCommunity,
            getCommunity: getCommunity,
            loadAllCommunities: loadAllCommunities,
            getAllCommunity: getAllCommunity,
            updateCommunity: updateCommunity,
            deleteCommunity: deleteCommunity,
            loadAllCommunitiesOfUser: loadAllCommunitiesOfUser,
            followCommunity: followCommunity,
            unfollowCommunity: unfollowCommunity,
            blockUser: blockUser,
            unblockUser: unblockUser
        };

        function blockUser(id, userId) {
            var deferred = $q.defer();
            $http.put(urls.COMMUNITY_SERVICE_API + '/' + id + '/block?userId=' + userId).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function unblockUser(id, userId) {
            var deferred = $q.defer();
            $http.put(urls.COMMUNITY_SERVICE_API + '/' + id + '/unblock?userId=' + userId).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function followCommunity(id) {
            var deferred = $q.defer();
            $http.put(urls.COMMUNITY_SERVICE_API + '/' + id + '/follow').then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function unfollowCommunity(id) {
            var deferred = $q.defer();
            $http.put(urls.COMMUNITY_SERVICE_API + '/' + id + '/unfollow').then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function createCommunity(community) {
            var deferred = $q.defer();
            $http.post(urls.COMMUNITY_SERVICE_API, community).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getCommunity(id) {
            var deferred = $q.defer();
            $http.get(urls.COMMUNITY_SERVICE_API + '/' + id).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadAllCommunities(page, size) {
            var deferred = $q.defer();
            $http.get(urls.COMMUNITY_SERVICE_API + '?page=' + page + '&size=' + size).then(
                function (response) {
                    $localStorage.communities = response.data.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getAllCommunity() {
            return $localStorage.communities;
        }

        function updateCommunity(community, id) {
            var deferred = $q.defer();
            $http.put(urls.COMMUNITY_SERVICE_API + '/' + id, community).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function deleteCommunity(id) {
            var deferred = $q.defer();
            $http.delete(urls.COMMUNITY_SERVICE_API + '/' + id).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadAllCommunitiesOfUser(id, page, size) {
            var deferred = $q.defer();
            var url;
            if (page === null && size === null) {
                url = urls.PROFILE_SERVICE_API + id + '/community';
            }
            else {
                url = urls.PROFILE_SERVICE_API + id + '/community?page=' + page + '&size=' + size
            }
            $http.get(url).then(
                function (response) {
                    $localStorage.communities = response.data.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }
    }
);