'use strict';

angular.module('socialNetworkApp').service('chatService', function ($rootScope, $q, $timeout, urls) {

    var service = {};

    var stompClient = null;

    var listener = $q.defer();

    service.connect = function () {
        initialize();
    };

    var reconnect = function () {
        $timeout(function () {
            initialize();
        }, 10000);
    };

    var initialize = function () {
        stompClient = Stomp.over(new SockJS(urls.SOCKET));
        stompClient.debug = null;
        stompClient.connect({}, startListener);
        stompClient.onclose = reconnect;
    };

    var startListener = function () {
        var destination = urls.SOCKET_SUBSCRIBE + $rootScope.principal.email;
        stompClient.subscribe(destination, function (data) {
            listener.notify(JSON.parse(data.body));
        });
    };

    service.send = function (message) {
        stompClient.send(urls.SOCKET_SEND, {}, JSON.stringify(message));
    };

    service.receive = function () {
        return listener.promise;
    };

    return service;
});