'use strict';

angular.module('socialNetworkApp').controller('PostEditModalCtrl',
    function ($rootScope, $scope, $uibModalInstance, regexPatterns, postService, post) {
        $scope.attachments = [];

        var self = this;

        self.post = post;

        self.save = save;
        self.cancel = cancel;
        self.remove = remove;

        function remove(item) {
            var index = self.post.attachments.indexOf(item);
            self.post.attachments.splice(index, 1);
        }

        function save() {
            postService.updatePost(self.post, $scope.attachments).then(
                function (response) {
                    $uibModalInstance.close(response);
                }
            )
        }

        function cancel() {
            $uibModalInstance.dismiss();
        }
    }
);