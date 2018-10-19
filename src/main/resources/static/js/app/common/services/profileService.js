'use strict';

angular.module('socialNetworkApp').factory('profileService',
    function ($localStorage, $q, $http, urls) {

        return {
            getProfile: getProfile,
            loadAllProfiles: loadAllProfiles,
            getAllProfiles: getAllProfiles,
            updateProfile: updateProfile
        };

        function getProfile(id) {
            var deferred = $q.defer();
            $http.get(urls.PROFILE_SERVICE_API + id).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadAllProfiles() {
            var deferred = $q.defer();
            $http.get(urls.PROFILE_SERVICE_API).then(
                function (response) {
                    $localStorage.profiles = response.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getAllProfiles() {
            return $localStorage.profiles;
        }

        function updateProfile(profile, id) {
            var deferred = $q.defer();
            $http.put(urls.PROFILE_SERVICE_API + id, profile).then(
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