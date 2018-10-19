<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" ng-click="ctrl.cancel()">×</button>
    <h4 class="modal-title">Communities</h4>
</div>
<div class="modal-body">
    <p class="lead" ng-show="!ctrl.getCommunities()">The user does not have any communities.</p>
    <div class="panel panel-default" ng-repeat="community in ctrl.getCommunities().content | orderBy:'-participantsCount'">
        <div class="panel-body">
            <div>
                    <span class="lead">
                        <a href="/#/community/{{community.id}}" ng-click="ctrl.cancel();">{{community.title}}</a>
                    </span>
            </div>
            <div>
                <span class="text-muted">{{community.type | lowercase}}</span>
            </div>
            <div>
                <span class="text-muted">{{community.participantsCount}} followers</span>
            </div>
        </div>
    </div>
</div>