'use strict';

angular.module('socialNetworkApp').controller('CommunityModalCtrl',
    function ($uibModalInstance, $location, regexPatterns, communityService, community, operation) {

        var self = this;

        self.community = community;
        self.operation = operation;
        self.titlePattern = regexPatterns.TITLE;
        self.message = '';
        self.error = '';

        self.create = function create() {
            communityService.createCommunity(self.community).then(
                function (response) {
                    self.message = 'The community was created successfully.';
                    self.error = '';
                    $uibModalInstance.close(response);
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            )
        };

        self.update = function update() {
            communityService.updateCommunity(self.community, self.community.id).then(
                function (response) {
                    self.message = 'The community was updated successfully.';
                    self.error = '';
                    $uibModalInstance.close(response);
                    $location.path('/community/' + response.data.id);
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            )
        };

        self.delete = function deleteIt() {
            communityService.deleteCommunity(self.community.id).then(
                function (response) {
                    self.message = 'The community was deleted successfully.';
                    self.error = '';
                    $uibModalInstance.close(response);
                    $location.path('/community');
                },
                function (errResponse) {
                    self.error = errResponse.data.error;
                    self.message = '';
                }
            )
        };

        self.cancel  = function cancel() {
            $uibModalInstance.dismiss();
        }
    }
);