'use strict';

angular.module('socialNetworkApp').service('fileService', function ($http, $q, urls) {
    this.uploadCommunityLogotype = function(id, file){
        var deferred = $q.defer();
        var fd = new FormData();
        fd.append('logotype', file);
        $http.post(urls.COMMUNITY_SERVICE_API + '/' + id + '/upload', fd, {
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
        return deferred.promise;
    };

    this.uploadAvatar = function(id, file){
        var deferred = $q.defer();
        var fd = new FormData();
        fd.append('avatar', file);
        $http.post(urls.PROFILE_SERVICE_API + id + '/avatar/upload', fd, {
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
        return deferred.promise;
    };

    this.deleteAvatar = function(id){
        var deferred = $q.defer();
        $http.delete(urls.PROFILE_SERVICE_API + id + '/avatar/delete').then(
        function (response) {
                deferred.resolve(response.data);
            },
            function (errResponse) {
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }
});