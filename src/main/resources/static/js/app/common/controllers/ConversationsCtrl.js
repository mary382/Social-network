'use strict';

angular.module('socialNetworkApp').controller('ConversationsCtrl',
    function ($rootScope, $timeout, conversationService, messageService, chatService, friendshipService) {

        var self = this;

        self.messages = [];
        self.newMessage = {};
        self.currentConversation = {};
        self.conversations = [];
        self.isBlocked = false;
        self.messagesPerPage = 10;
        self.currentPage = 1;

        self.initConversation = initConversation;
        self.switchConversation = switchConversation;
        self.initMessages = initMessages;
        self.loadMoreMessages = loadMoreMessages;
        self.isLastPage = isLastPage;
        self.getMessages = getMessages;
        self.sendMessage = sendMessage;
        self.getConversation = getConversation;
        self.getAllConversations = getAllConversations;
        self.chooseDateFormat = chooseDateFormat;

        chatService.receive().then(null, null, function (message) {
            if (message.statuses[$rootScope.principal.id] !== 'BLOCKED') {
                conversationService.loadAllConversationsOfPrincipal();
                if (message.conversationId === self.currentConversation.id) {
                    messageService.getMessages() === undefined ?
                        messageService.loadMessagesOfConversation(
                            self.currentConversation.id,
                            self.currentPage,
                            self.messagesPerPage) :
                        messageService.pushMessage(message);
                    if (message.author.id !== $rootScope.principal.id) {
                        $timeout(function () {
                            conversationService.resetUnreadMessagesQuantity(self.currentConversation.id);
                        }, 3000);
                    }
                }
            }
        });

        function initConversation() {
            var conversation = conversationService.getCurrentConversation();
            if (conversation !== undefined) {
                self.currentConversation = conversation;
                self.initMessages();
            }
            if (conversation.participants.length === 2) {
                checkBlock(conversation);
            }
        }

        function switchConversation(conversation) {
            self.newMessage = '';
            self.currentConversation = conversation;
            self.currentPage = 1;
            conversationService.resetUnreadMessagesQuantity(self.currentConversation.id);
            self.initMessages();
            self.isBlocked = false;
            if (conversation.participants.length === 2) {
                checkBlock(conversation);
            }
        }

        function checkBlock(conversation) {
            angular.forEach(conversation.participants, function (value, key) {
                if (value.id !== $rootScope.principal.id) {
                    friendshipService.loadFriendshipStatus(value.id, $rootScope.principal.id).then(
                        function () {
                            self.isBlocked = (friendshipService.getStatus() === 4 ||
                                friendshipService.getStatus() === 6);
                        }
                    );
                }
            })
        }

        function initMessages() {
            messageService.loadMessagesOfConversation(
                self.currentConversation.id,
                self.currentPage,
                self.messagesPerPage);
        }

        function loadMoreMessages() {
            messageService.loadMoreMessagesOfConversation(
                self.currentConversation.id,
                ++self.currentPage,
                self.messagesPerPage);
        }

        function isLastPage() {
            return messageService.isLastPage();
        }

        function getMessages() {
            return messageService.getMessages();
        }

        function sendMessage() {
            self.newMessage.conversationId = self.currentConversation.id;
            chatService.send(self.newMessage);
            self.newMessage = {};
        }

        function getConversation(id) {
            conversationService.getConversation(id).then(
                function (conversation) {
                    self.currentConversation = conversation;
                }
            );
        }

        function getAllConversations() {
            return conversationService.getConversations();
        }

        function chooseDateFormat(time) {
            var today = new Date();
            today.setHours(0, 0, 0, 0);
            var thisYear = new Date();
            thisYear.setMonth(0, 1);
            thisYear.setHours(0, 0, 0, 0);
            return time < thisYear.getTime() ?
                'HH:mm (MMM d, y)' : time < today.getTime() ?
                    'HH:mm (MMM d)' : 'HH:mm';
        }
    }
);