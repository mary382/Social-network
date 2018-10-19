<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" ng-click="ctrl.cancel()">×</button>
    <h4 class="modal-title">Followers</h4>
</div>
<div class="modal-body" ng-init="ctrl.initCommunity()">
    <div class="panel panel-default" ng-repeat="participant in ctrl.participants">
        <div class="panel-body">
            <div>
                <span class="lead">
                    <a href="/#/profile/{{participant.id}}" ng-click="ctrl.cancel()">
                        {{participant.firstName}} {{participant.lastName}}
                    </a>
                </span>
            </div>
        </div>
    </div>
    <ul uib-pagination total-items="ctrl.totalParticipants" ng-model="ctrl.currentPage"
        ng-change="ctrl.participantPageChanged()" class="pagination-sm" items-per-page="ctrl.participantsPerPage"></ul>
</div>