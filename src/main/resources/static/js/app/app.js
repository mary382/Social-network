'use strict';

var app = angular.module('socialNetworkApp', ['ui.router', 'ui.bootstrap', 'ngStorage', 'ngAnimate',
    'ngSanitize', 'ui.bootstrap.datetimepicker']);

app.constant('regexPatterns', {
    EMAIL: /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/,
    PASSWORD: /^(?=.*\d)(?=.*[A-z])[0-9a-zA-Z]{6,60}$/,
    PROPER_NOUN: /^[A-z'-]{2,30}$/,
    TITLE: /^[A-z][ A-z0-9]{2,30}$/,
    ID: /^[1-9]{1}[0-9]*$/
}).constant('urls', {
    COMMUNITY_SERVICE_API: '/api/community',
    CONVERSATION_SERVICE_API: '/api/conversation/',
    CONVERSATION_UNOPENED_SERVICE_API: '/api/user/principal/conversation/unopenedMessagesQuantity/',
    FRIENDSHIP_SERVICE_API: '/api/friendship/',
    MESSAGE_SERVICE_API: '/api/message/',
    POST_SERVICE_API: '/api/post/',
    PRINCIPAL_API: '/api/profile/principal/',
    PRINCIPAL_CONVERSATIONS_API: '/api/user/principal/conversation/',
    PROFILE_SERVICE_API: '/api/profile/',
    SOCKET: '/chat',
    SOCKET_SEND: '/app/chat',
    SOCKET_SUBSCRIBE: '/topic/messages/',
    USER_SERVICE_API: '/api/user/'
});

app.config(function ($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise('/');

        $stateProvider
            .state('home', {
                controller: 'HomeCtrl',
                controllerAs: 'ctrl',
                url: '/',
                templateUrl: 'partials/home'
            })
            .state('news', {
                url: '/news',
                templateUrl: 'partials/feed',
                controller: 'FeedCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    posts: function ($q, $rootScope, postService) {
                        var deferred = $q.defer();
                        postService.loadUserNews($rootScope.principal.id, 1, 10).then(deferred.resolve, deferred.resolve);
                        return deferred.promise;
                    }
                }
            })
            .state('profile', {
                url: '/profile/{id:int}',
                templateUrl: 'partials/profile',
                controller: 'ProfileCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    posts: function ($q, $stateParams, postService) {
                        var deferred = $q.defer();
                        postService.newloadAllPostsOfUser($stateParams.id, 1, 5).then(deferred.resolve, deferred.resolve);
                        return deferred.promise;
                    }
                }
            })
            .state('conversations', {
                url: '/conversations',
                templateUrl: 'partials/conversations',
                controller: 'ConversationsCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    conversations: function ($q, conversationService) {
                        var deferred = $q.defer();
                        conversationService.loadAllConversationsOfPrincipal()
                            .then(deferred.resolve, deferred.resolve);
                        return deferred.promise;
                    }
                }
            })
            .state('profiles', {
                url: '/profiles',
                templateUrl: 'partials/profiles',
                controller: 'ProfileCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    users: function ($q, profileService) {
                        var deferred = $q.defer();
                        profileService.loadAllProfiles().then(deferred.resolve, deferred.resolve);
                        return deferred.promise;
                    }
                }
            })
            .state('community', {
                url: '/community/{id:int}',
                templateUrl: 'partials/community',
                controller: 'CommunityCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    posts: function ($q, $stateParams, postService) {
                        var deffered = $q.defer();
                        postService.loadAllPostsOfCommunity($stateParams.id, 1, 5).then(deffered.resolve, deffered.resolve);
                        return deffered.promise;
                    }
                }
            })
            .state('user_communities', {
                url: '/profile/{id:int}/community',
                templateUrl: 'partials/communities',
                controller: 'CommunityListCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    flag: function () {
                        return true;
                    },
                    communities: function ($q, $stateParams, communityService) {
                        var deferred = $q.defer();
                        communityService.loadAllCommunitiesOfUser($stateParams.id, 1, 20).then(deferred.resolve, deferred.resolve);
                        return deferred.promise;
                    }
                }
            })
            .state('communities', {
                url: '/community',
                templateUrl: 'partials/communities',
                controller: 'CommunityListCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    flag: function () {
                        return false;
                    },
                    communities: function ($q, communityService) {
                        var deferred = $q.defer();
                        communityService.loadAllCommunities(1, 20).then(deferred.resolve, deferred.resolve);
                        return deferred.promise;
                    }
                }
            })
            .state('community_settings', {
                url: '/community/{id:int}/settings',
                templateUrl: 'partials/community-settings',
                controller: 'CommunitySettingsCtrl',
                controllerAs: 'ctrl'
            })
            .state('friends', {
                url: '/profile/principal/friends',
                templateUrl: 'partials/friends',
                controller: 'FriendshipCtrl',
                controllerAs: 'ctrl',
                resolve: {
                    users: function ($q, $rootScope, friendshipService) {
                        var deferred = $q.defer();
                        friendshipService.loadFriends($rootScope.principal.id).then(deferred.resolve, deferred.resolve);
                        return deferred.promise;
                    }
                }
            })
            .state('403', {
                url: '/403',
                templateUrl: 'partials/403'
            })
            .state('404', {
                url: '/404',
                templateUrl: 'partials/404'
            })
            .state('register', {
                url: '/register',
                templateUrl: 'partials/register',
                controller: 'UserCtrl',
                controllerAs: 'ctrl'
            })
            .state('login', {
                url: '/login',
                templateUrl: 'partials/login',
                controller: 'SecurityCtrl',
                controllerAs: 'ctrl'
            });
    }
);