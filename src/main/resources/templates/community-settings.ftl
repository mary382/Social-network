<div class="generic-container">
    <div class="col-md-6 col-md-offset-3">
        <div ng-init="ctrl.initCommunity()">
            <div class="alert alert-success" role="alert" ng-if="ctrl.message">
                {{ctrl.message}}
            </div>
            <div class="alert alert-danger" role="alert" ng-if="ctrl.error">
                {{ctrl.error}}
            </div>
            <div class="panel panel-default" >
                <div class="form-group pull-right">
                    <a class="btn btn-info" ng-click="ctrl.openUpdateModal()">Update community</a>
                    <a class="btn btn-danger" ng-click="ctrl.openDeleteModal()">Delete community</a>
                </div>
            </div>
            <form name="outerForm" class="tab-form-demo">
                <uib-tabset active="activeForm">
                    <uib-tab index="0" heading="Blacklist">
                        <br>
                        <div class="panel panel-default" ng-repeat="user in ctrl.blacklist">
                            <div class="panel-body">
                                <a href="/#/profile/{{user.id}}">{{user.firstName}} {{user.lastName}}</a>
                                <button class="btn btn-default pull-right"
                                        ng-click="ctrl.unblockUser(user.id)">Unblock</button>
                            </div>
                        </div>
                        <ul uib-pagination total-items="ctrl.blacklistTotal" ng-model="ctrl.currentPage"
                            ng-show="ctrl.blacklistTotal != 0" ng-change="ctrl.blacklistPageChanged()"
                            class="pagination-sm" items-per-page="ctrl.usersPerPage"></ul>
                    </uib-tab>
                    <uib-tab index="1" heading="Block user">
                        <ng-form name="nestedForm">
                            <label>Id</label>
                            <input type="text" class="form-control" ng-pattern="ctrl.idPattern" required ng-model="id"/>
                            <button class="btn btn-default" ng-click="ctrl.blockUser(id)"
                                    ng-disabled="nestedForm.$invalid">Block</button>
                        </ng-form>
                    </uib-tab>
                </uib-tabset>
            </form>
        </div>
    </div>
</div>