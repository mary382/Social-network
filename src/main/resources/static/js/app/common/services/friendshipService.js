'use  strict';

angular.module('socialNetworkApp').factory('friendshipService',
    function ($localStorage, $q, $http, urls) {
        return {
            loadFriendshipStatus: loadFriendshipStatus,
            getStatus: getStatus,
            getStatusMessage: getStatusMessage,
            loadMutualFriends: loadMutualFriends,
            addFriend: addFriend,
            deleteFriend: deleteFriend,
            loadBlacklist: loadBlacklist,
            loadOutgoingRequests: loadOutgoingRequests,
            loadIncomingRequests: loadIncomingRequests,
            approveAllIncomingRequest: approveAllIncomingRequest,
            declineAllIncomingRequest: declineAllIncomingRequest,
            declineAllOutgoingRequest: declineAllOutgoingRequest,
            loadFriends: loadFriends,
            blockUser: blockUser,
            unblockUser: unblockUser,
            getUsers: getUsers,
            remove: remove,
            getBlacklist: getBlacklist
        };

        function loadFriendshipStatus(userId, friendId) {
            var deferred = $q.defer();
            $http.get(urls.FRIENDSHIP_SERVICE_API + 'status?userId=' + userId + '&friendId=' + friendId).then(
                function (response) {
                    $localStorage.status = response.data.data.key;
                    $localStorage.statusMessage = response.data.data.value;
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getStatus() {
            return $localStorage.status;
        }
        function getStatusMessage() {
            return $localStorage.statusMessage;
        }

        function loadMutualFriends(userId, friendId) {
            var deferred = $q.defer();
            $http.get(urls.FRIENDSHIP_SERVICE_API + 'mutualFriends?userId=' + userId + '&friendId=' + friendId).then(
                function (response) {
                    $localStorage.users = response.data;
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function addFriend(id) {
            var deferred = $q.defer();
            $http.post(urls.PRINCIPAL_API + 'friend/add/' + id).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function deleteFriend(id) {
            var deferred = $q.defer();
            $http.put(urls.PRINCIPAL_API + 'friend/delete/' + id).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadBlacklist() {
            var deferred = $q.defer();
            $http.get(urls.PRINCIPAL_API + 'blacklist').then(
                function (response) {
                    $localStorage.blacklist = response.data;
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadOutgoingRequests() {
            var deferred = $q.defer();
            $http.get(urls.PRINCIPAL_API + 'request/outgoing').then(
                function (response) {
                    $localStorage.users = response.data;
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadIncomingRequests(id) {
            var deferred = $q.defer();
            $http.get(urls.PROFILE_SERVICE_API + id + '/request/incoming').then(
                function (response) {
                    $localStorage.users = response.data;
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function approveAllIncomingRequest() {
            var deferred = $q.defer();
            $http.put(urls.PRINCIPAL_API + 'request/incoming/approve').then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function declineAllIncomingRequest() {
            var deferred = $q.defer();
            $http.put(urls.PRINCIPAL_API + 'request/incoming/decline').then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function declineAllOutgoingRequest() {
            var deferred = $q.defer();
            $http.put(urls.PRINCIPAL_API + 'request/outgoing/decline').then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadFriends(id) {
            var deferred = $q.defer();
            $http.get(urls.PROFILE_SERVICE_API + id + '/friend/').then(
                function (response) {
                    $localStorage.users = response.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getUsers() {
            return $localStorage.users;
        }

        function getBlacklist() {
            return $localStorage.blacklist;
        }

        function remove(user) {
            var index = $localStorage.users.data.content.indexOf(user);
            $localStorage.users.data.content.splice(index, 1);
        }

        function blockUser(id, time) {
            var deferred = $q.defer();
            if (time === null) {
                var url = urls.PROFILE_SERVICE_API + id + '/block';
            }
            else {
                url = urls.PROFILE_SERVICE_API + id + '/block?time=' + time.getTime();
            }
            $http.post(url).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function unblockUser(id) {
            var deferred = $q.defer();
            $http.put(urls.PROFILE_SERVICE_API + id + '/unblock').then(
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
