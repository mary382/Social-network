'use strict';

angular.module('socialNetworkApp').factory('conversationService',
    function ($localStorage, $q, $http, urls) {

        return {
            createConversation: createConversation,
            getConversation: getConversation,
            getCurrentConversation: getCurrentConversation,
            loadAllConversationsOfPrincipal: loadAllConversationsOfPrincipal,
            getConversations: getConversations,
            resetUnreadMessagesQuantity: resetUnreadMessagesQuantity,
            loadUnopenedConversationsQuantity: loadUnopenedConversationsQuantity,
            getUnopenedConversationsQuantity: getUnopenedConversationsQuantity
        };

        function createConversation(conversation) {
            var deferred = $q.defer();
            $http.post(urls.CONVERSATION_SERVICE_API, conversation).then(
                function (response) {
                    $localStorage.currentConversation = response.data.data;
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getCurrentConversation() {
            var conversation = $localStorage.currentConversation;
            delete $localStorage.currentConversation;
            return conversation;
        }

        function getConversation(id) {
            var deferred = $q.defer();
            $http.get(urls.CONVERSATION_SERVICE_API + id).then(
                function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadAllConversationsOfPrincipal() {
            delete $localStorage.conversations;
            var deferred = $q.defer();
            $http.get(urls.PRINCIPAL_CONVERSATIONS_API).then(
                function (response) {
                    $localStorage.conversations = response.data.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getConversations() {
            return $localStorage.conversations;
        }

        function resetUnreadMessagesQuantity(id) {
            $http.post(urls.CONVERSATION_SERVICE_API + id + '/resetUnopenedMessagesQuantity/').then(
                function () {
                    loadUnopenedConversationsQuantity();
                    loadAllConversationsOfPrincipal();
                }
            )
        }

        function loadUnopenedConversationsQuantity() {
            var deferred = $q.defer();
            $http.get(urls.CONVERSATION_UNOPENED_SERVICE_API).then(
                function (response) {
                    $localStorage.unopenedConversationsQuantity = response.data.data;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function getUnopenedConversationsQuantity() {
            return $localStorage.unopenedConversationsQuantity;
        }
    }
);