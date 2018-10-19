<div class="generic-container">
    <div class="col-md-6 col-md-offset-3">
        <form class="form-search">
            <div class="form-group">
                <input class="form-control input-lg" placeholder="Search..." ng-model="searchText">
                <br>
                <form name="outerForm" class="tab-form-demo">
                    <uib-tabset active="activeForm">
                        <uib-tab index="0" select="ctrl.tabSelect(0)" heading="My friends">
                            <br>
                            <p class="lead" ng-show="ctrl.getUsers().data.content.length === 0 || !ctrl.getUsers()">
                                The user does not have any friends.</p>
                            <ul class="list-group">
                                <li class="list-group-item"
                                    ng-repeat="friend in ctrl.getUsers().data.content | filter:searchText | orderBy:'-id'">
                                    <img ng-src="{{friend.imageUrl == null ? '/images/default-avatar.png' : friend.imageUrl}}"
                                         class="profile-photo-md">
                                    <a href="/#/profile/{{friend.id}}" ng-click="ctrl.cancel()" style="color: #444444;">
                                        {{friend.firstName}} {{friend.lastName}}
                                    </a>
                                    <div class="btn-group pull-right" uib-dropdown>
                                        <a class="btn btn-default" ng-click="ctrl.deleteFriend(friend)">Delete</a>
                                        <a class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                                           uib-dropdown-toggle><span class="caret"></span></a>
                                        <ul class="dropdown-menu pull-right" uib-dropdown-menu>
                                            <li><a ng-click="ctrl.openBlockUserModal(friend, 0)">Block</a></li>
                                        </ul>
                                    </div>
                                </li>
                            </ul>
                        </uib-tab>
                        <uib-tab index="1" select="ctrl.tabSelect(1)" heading="Followers">
                            <br>
                            <p class="lead" ng-show="ctrl.getUsers().data.content.length === 0 || !ctrl.getUsers()">
                                The user does not have any followers.</p>
                            <div class="text-center"
                                 ng-show="ctrl.getUsers().data.content.length > 0">
                                <a class="btn btn-primary" ng-click="ctrl.approveAllIncomingRequest()">Approve all</a>
                                <a class="btn btn-primary" ng-click="ctrl.declineAllIncomingRequest()">Decline all</a>
                                <br>
                            </div>
                            <ul class="list-group">
                                <li class="list-group-item"
                                    ng-repeat="friend in ctrl.getUsers().data.content | filter:searchText | orderBy:'-id'">
                                    <img ng-src="{{friend.imageUrl == null ? '/images/default-avatar.png' : friend.imageUrl}}"
                                         class="profile-photo-md">
                                    <a href="/#/profile/{{friend.id}}" ng-click="ctrl.cancel()" style="color: #444444;">
                                        {{friend.firstName}} {{friend.lastName}}
                                    </a>
                                    <div class="btn-group pull-right" uib-dropdown>
                                        <a class="btn btn-default" ng-click="ctrl.addFriend(friend)">Approve</a>
                                        <a class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                                           uib-dropdown-toggle><span class="caret"></span></a>
                                        <ul class="dropdown-menu pull-right" uib-dropdown-menu>
                                            <li><a ng-click="ctrl.deleteFriend(friend)">Decline</a></li>
                                            <li><a ng-click="ctrl.openBlockUserModal(friend, 0)">Block</a></li>
                                        </ul>
                                    </div>
                                </li>
                            </ul>
                        </uib-tab>
                        <uib-tab index="2" select="ctrl.tabSelect(2)" heading="Outgoing requests">
                            <br>
                            <p class="lead" ng-show="ctrl.getUsers().data.content.length === 0 || !ctrl.getUsers()">
                                The user does not have any outgoing requests.</p>
                            <div class="text-center"
                                 ng-show="ctrl.getUsers().data.content.length > 0">
                                <a class="btn btn-primary" ng-click="ctrl.declineAllOutgoingRequest()">Decline all</a>
                                <br>
                            </div>
                            <ul class="list-group">
                                <li class="list-group-item"
                                    ng-repeat="friend in ctrl.getUsers().data.content | filter:searchText | orderBy:'-id'">
                                    <img ng-src="{{friend.imageUrl == null ? '/images/default-avatar.png' : friend.imageUrl}}"
                                         class="profile-photo-md">
                                    <a href="/#/profile/{{friend.id}}" ng-click="ctrl.cancel()" style="color: #444444;">
                                        {{friend.firstName}} {{friend.lastName}}
                                    </a>
                                    <div class="btn-group pull-right" uib-dropdown>
                                        <a class="btn btn-default" ng-click="ctrl.deleteFriend(friend)">Decline</a>
                                        <a class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                                           uib-dropdown-toggle><span class="caret"></span></a>
                                        <ul class="dropdown-menu pull-right" uib-dropdown-menu>
                                            <li><a ng-click="ctrl.openBlockUserModal(friend, 0)">Block</a></li>
                                        </ul>
                                    </div>
                                </li>
                            </ul>
                        </uib-tab>
                        <uib-tab index="3" select="ctrl.tabSelect(3)" heading="Blacklist">
                            <br>
                            <p class="lead" ng-show="ctrl.getBlacklist().data.length === 0 || !ctrl.getBlacklist()">
                                The user does not block anyone.</p>
                            <ul class="list-group">
                                <li class="list-group-item"
                                    ng-repeat="friend in ctrl.getBlacklist().data | filter:searchText | orderBy:'-id'">
                                    <img ng-src="{{friend.key.imageUrl == null ? '/images/default-avatar.png' : friend.key.imageUrl}}"
                                         class="profile-photo-md">
                                    <a href="/#/profile/{{friend.key.id}}" ng-click="ctrl.cancel()"
                                       style="color: #444444;">
                                        {{friend.key.firstName}} {{friend.key.lastName}}</a>
                                    {{friend.value}}
                                    <div class="btn-group pull-right" uib-dropdown>
                                        <a class="btn btn-default" ng-click="ctrl.unblockUser(friend.key)">Unblock</a>
                                        <a class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                                           uib-dropdown-toggle><span class="caret"></span></a>
                                        <ul class="dropdown-menu pull-right" uib-dropdown-menu>
                                            <li><a ng-click="ctrl.openBlockUserModal(friend.key, 1)">Block</a></li>
                                        </ul>
                                    </div>
                                </li>
                            </ul>
                        </uib-tab>
                    </uib-tabset>
                </form>

            </div>
            <br>
        </form>
    </div>
</div>