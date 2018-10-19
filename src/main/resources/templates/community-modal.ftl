<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" ng-click="ctrl.cancel()">×</button>
    <h4 class="modal-title">{{ctrl.operation}} community</h4>
</div>
<div class="modal-body" ng-show="ctrl.operation != 'Delete'">
    <div class="alert alert-success" role="alert" ng-if="ctrl.message">
        {{ctrl.message}}
    </div>
    <div class="alert alert-danger" role="alert" ng-if="ctrl.error">
        {{ctrl.error}}
    </div>
    <form name="createCommunityForm" novalidate>
        <div class="form-group">
            <label for="title">Title:</label>
            <input id="title" name="title" class="form-control" placeholder="Title" ng-pattern="ctrl.titlePattern"
                   required ng-model="ctrl.community.title"
                   uib-popover="The title can consist only of letters and numbers."
                   popover-animation popover-trigger="'focus'" popover-placement="top-left"/>
            <span class="validation-tip" ng-show="createCommunityForm.title.$dirty && createCommunityForm.title.$invalid">
                <span ng-show="createCommunityForm.title.$error.required">Title is required.</span>
                <span ng-show="createCommunityForm.title.$error.pattern">
                    The title must begin with a letter and have a length of at least 3 characters
                </span>
            </span>
        </div>
        <div class="form-group">
            <label for="info">Info:</label>
            <textarea id="info" name="info" rows="1" class="form-control" placeholder="Info"
                      ng-model="ctrl.community.info"/>
        </div>
        <div class="form-group">
            <label for="type">Type:</label>
            <select class="form-control" ng-model="ctrl.community.type">
                <option>OPEN</option>
                <option>CLOSED</option>
            </select>
        </div>
    </form>
</div>
<div class="modal-footer">
    <button type="button" class="btn btn-default" ng-click="ctrl.cancel()">Cancel</button>
    <button type="button" class="btn btn-success" ng-show="ctrl.operation == 'Update'"
            ng-click="ctrl.update()" ng-disabled="createCommunityForm.$invalid">Update</button>
    <button type="button" class="btn btn-danger" ng-show="ctrl.operation == 'Delete'"
            ng-click="ctrl.delete()">Delete</button>
    <button type="button" class="btn btn-success" ng-show="ctrl.operation == 'Create'"
            ng-click="ctrl.create()" ng-disabled="createCommunityForm.$invalid">Create</button>
</div>