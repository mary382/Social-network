'use strict';

angular.module('socialNetworkApp').controller('UserCtrl',
    function ($scope, regexPatterns, userService, $location) {

        var self = this;

        self.user = {};
        self.users = [];

        self.register = register;
        self.createUser = createUser;
        self.getUser = getUser;
        self.getAllUsers = getAllUsers;
        self.updateUser = updateUser;

        self.successMessage = '';
        self.errorMessage = '';

        self.emailPattern = regexPatterns.EMAIL;
        self.passwordPattern = regexPatterns.PASSWORD;
        self.properNounPattern = regexPatterns.PROPER_NOUN;

        function register() {
            createUser(self.user);
            $scope.registerForm.$setPristine();
        }

        function createUser(user) {
            userService.createUser(user).then(
                function (response) {
                    // self.successMessage = 'You have successfully registered.';
                    $location.path("/login");
                    self.errorMessage = '';
                    self.user = {};
                },
                function (errResponse) {
                    self.errorMessage = 'Failed to register: ' + errResponse.data.errorMessage;
                    self.successMessage = '';
                }
            );
        }

        function getUser(id) {
            userService.getUser(id).then(
                function (user) {
                    return user;
                }
            );
        }

        function getAllUsers() {
            return userService.getAllUsers();
        }

        function updateUser(user, id) {
            userService.updateUser(user, id).then(
                function (response) {
                    self.successMessage = 'User updated successfully.';
                    self.errorMessage = '';
                },
                function (errResponse) {
                    self.errorMessage = 'Error while updating user. ' + errResponse.data;
                    self.successMessage = '';
                }
            );
        }
    }
);