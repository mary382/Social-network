'use strict';

angular.module('socialNetworkApp').controller('BlockUserModalCtrl',
    function ($rootScope, $scope, $stateParams, $uibModalInstance, regexPatterns, friendshipService, user, flag) {

        var self = this;

        self.block = block;
        self.cancel = cancel;

        self.time = null;
        self.minDate = new Date();

        function block() {
            if (self.radioModel.localeCompare('Permanently') === 0) {
                self.time = null;
            }
            friendshipService.blockUser(user.id, self.time).then(
                function (response) {
                    if ($stateParams.id !== undefined && $rootScope.principal.id !== $stateParams.id) {
                        friendshipService.loadFriendshipStatus($rootScope.principal.id, $stateParams.id);
                    }
                    else {
                        if (flag === 0) {
                            friendshipService.remove(user);
                        } else {
                            friendshipService.loadBlacklist();
                            friendshipService.getBlacklist();
                        }
                    }
                    $uibModalInstance.close(response);
                }
            )
        }

        function cancel() {
            $uibModalInstance.dismiss();
        }

        self.radioModel = 'Permanently';

        this.isOpen = false;

        this.openCalendar = function (e) {
            e.preventDefault();
            e.stopPropagation();

            self.isOpen = true;
        };

        self.datePicker = {
            options: {
                minDate: new Date(),
            }
        }
    }
);