'use strict';

angular.module('socialNetworkApp').controller('ProfileEditModalCtrl',
    function ($scope, $rootScope, $uibModalInstance, regexPatterns, profileService, profile, fileService) {

        $scope.avatar = undefined;
        var self = this;

        self.profile = profile;

        self.properNounPattern = regexPatterns.PROPER_NOUN;

        self.save = save;
        self.cancel = cancel;
        self.deleteAvatar = deleteAvatar;
        self.check = check;

        function save() {
            profileService.updateProfile(self.profile, $rootScope.principal.id, $scope.avatar).then(
                function (response) {
                    if ($scope.avatar !== undefined) {
                        fileService.uploadAvatar($rootScope.principal.id, $scope.avatar).then(
                            function (response) {
                                profileService.getProfile($rootScope.principal.id).then(
                                    function (profile) {
                                        self.profile = profile;
                                        $uibModalInstance.close(self.profile);
                                    }
                                );
                            }
                        );
                    } else {
                        $uibModalInstance.close(self.profile);
                    }

                }
            )
        }

        function cancel() {
            $uibModalInstance.dismiss();
        }

        function deleteAvatar() {
            fileService.deleteAvatar($rootScope.principal.id).then(
                function (response) {
                    self.profile.imageUrl = null;
                }
            )
        }

        function check() {
            if (self.profile.imageUrl === null) {
                return 1;
            }
            return 0;
        }

        this.isOpen = false;


        this.openCalendar = function (e) {
            e.preventDefault();
            e.stopPropagation();

            self.isOpen = true;
        };

        self.datePicker = {
            options: {
                maxDate: new Date(),
            }
        }
    }
);