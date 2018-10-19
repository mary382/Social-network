<div class="generic-container">
    <div class="col-md-6 col-md-offset-3">
        <form class="form-search">
            <div class="form-group">
                <input class="form-control input-lg" placeholder="Search..." ng-model="searchText">
                <br>
                <div class="form-group pull-right" ng-show="authenticated">
                    <a class="btn btn-primary" href="" ng-click="ctrl.openCreateModal()">Create community</a>
                    <a class="btn btn-default" href="/#/community">All communities</a>
                    <a class="btn btn-default" href="/#/profile/{{principal.id}}/community">My communities</a>
                </div>
            </div>
            <br>
            <div class="panel panel-default profile-anim" ng-repeat="community in ctrl.communities.content | filter:searchText | orderBy:'-participantsCount'">
                <div class="panel-body">
                    <div>
                        <span class="lead">
                            <a href="/#/community/{{community.id}}">{{community.title}}</a>
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
            <ul uib-pagination ng-show="ctrl.totalCommunities != 0" total-items="ctrl.totalCommunities" ng-model="ctrl.currentPage"
                ng-change="ctrl.communityPageChanged()" class="pagination-sm" items-per-page="ctrl.communitiesPerPage"></ul>
        </form>
    </div>
</div>