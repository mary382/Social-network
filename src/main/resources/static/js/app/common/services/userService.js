'use strict';

angular.module('socialNetworkApp').factory('userService',
    function ($localStorage, $q, $http, urls) {

        return {
            createUser: createUser,
            getUser: getUser,
            loadAllUsers: loadAllUsers,
            getAllUsers: getAllUsers,
            updateUser: updateUser,
            deleteUser: deleteUser
        };

        function createUser(user) {
            var deferred = $q.defer();
            $http.post(urls.USER_SERVICE_API, user).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getUser(id) {
            var deferred = $q.defer();
            $http.get(urls.USER_SERVICE_API + id).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadAllUsers() {
            var deferred = $q.defer();
            $http.get(urls.USER_SERVICE_API).then(
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

        function getAllUsers() {
            return $localStorage.users;
        }

        function updateUser(user, id) {
            var deferred = $q.defer();
            $http.put(urls.USER_SERVICE_API + id, user).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function deleteUser(id) {
            var deferred = $q.defer();
            $http.delete(urls.USER_SERVICE_API + id).then(
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