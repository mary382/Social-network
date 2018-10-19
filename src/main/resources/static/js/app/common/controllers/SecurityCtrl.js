'use strict';

angular.module('socialNetworkApp').controller('SecurityCtrl',
    function ($rootScope, $q, $http, $location, $state, urls, chatService, conversationService, friendshipService) {

        var self = this;

        self.credentials = {};
        self.notificationSound = new Audio('/audio/notification.mp3');

        var authenticate = function () {
            var deferred = $q.defer();
            $http.get(urls.PRINCIPAL_API).then(
                function (response) {
                    if (response.data.id) {
                        $rootScope.authenticated = true;
                        $rootScope.principal = response.data;
                        chatService.connect();
                        conversationService.loadUnopenedConversationsQuantity();
                        self.messagesNotifications = conversationService.getUnopenedConversationsQuantity();
                    } else {
                        $rootScope.authenticated = false;
                    }
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;

        };

        authenticate();

        self.login = function () {
            $http({
                url: 'login',
                method: 'POST',
                params: self.credentials
            }).success(function () {
                self.error = false;
                $rootScope.authenticated = true;
                authenticate().then(
                  function () {
                      $state.go('news');
                  }
                );
            }).error(function () {
                $location.path('/login');
                self.error = true;
                $rootScope.authenticated = false;
            });
        };

        self.logout = function () {
            $http.post('logout', {}).finally(function () {
                $rootScope.authenticated = false;
                delete $rootScope.principal;
                $location.path('/');
            });
        };

        self.getUnopenedConversationsQuantity = function () {
            return conversationService.getUnopenedConversationsQuantity();
        };

        chatService.receive().then(null, null, function (message) {
            if ($rootScope.principal.id !== message.author.id) {
                friendshipService.loadFriendshipStatus($rootScope.principal.id, message.author.id).then(
                    function () {
                        if (friendshipService.getStatus() !== 4 || friendshipService.getStatus() !== 6) {
                            conversationService.loadUnopenedConversationsQuantity();
                                self.notificationSound.play();
                        }
                    }
                );
            }
            else {
                conversationService.loadUnopenedConversationsQuantity();
            }
        });
    }
);