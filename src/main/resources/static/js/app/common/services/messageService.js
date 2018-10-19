'use strict';

angular.module('socialNetworkApp').factory('messageService',
    function ($localStorage, $q, $http, urls) {

        return {
            createMessage: createMessage,
            loadAllMessagesOfUser: loadAllMessagesOfUser,
            loadMessagesOfConversation: loadMessagesOfConversation,
            loadMoreMessagesOfConversation: loadMoreMessagesOfConversation,
            isLastPage: isLastPage,
            getMessages: getMessages,
            pushMessage: pushMessage,
            removeMessage: removeMessage
        };

        function createMessage(message) {
            var deferred = $q.defer();
            $http.post(urls.MESSAGE_SERVICE_API, message).then(
                function (response) {
                    deferred.resolve(response.data.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadAllMessagesOfUser(userId) {
            var deferred = $q.defer();
            $http.get(urls.USER_SERVICE_API + userId + '/message/').then(
                function (response) {
                    $localStorage.messages = response.data.data.content;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadMessagesOfConversation(conversationId, page, size) {
            delete $localStorage.messages;
            var deferred = $q.defer();
            $http.get(urls.CONVERSATION_SERVICE_API + conversationId + '/message?page=' + page + '&size=' + size).then(
                function (response) {
                    $localStorage.messages = response.data.data.content;
                    $localStorage.isLastPage = response.data.data.last;
                    deferred.resolve(response);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }

        function loadMoreMessagesOfConversation(conversationId, page, size) {
            $http.get(urls.CONVERSATION_SERVICE_API + conversationId + '/message?page=' + page + '&size=' + size).then(
                function (response) {
                    $localStorage.isLastPage = response.data.data.last;
                    angular.forEach(response.data.data.content, function(value) {
                        pushMessage(value);
                    })
                }
            )
        }

        function isLastPage() {
            return $localStorage.isLastPage;
        }

        function getMessages() {
            return $localStorage.messages;
        }

        function pushMessage(message) {
            return $localStorage.messages.push(message);
        }

        function removeMessage(id) {
            var deferred = $q.defer();
            $http.delete(urls.MESSAGE_SERVICE_API + id).then(
                function (response) {
                    deferred.resolve(response.data.data);
                },
                function (errResponse) {
                    deferred.reject(errResponse);
                }
            );
            return deferred.promise;
        }
    }
);