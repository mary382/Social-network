<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" ng-click="ctrl.cancel()">×</button>
    <h4 class="modal-title">Followers</h4>
</div>
<div class="modal-body">
    <p class="lead" ng-show="ctrl.getUsers().data.content.length === 0 || !ctrl.getUsers()">The user does not have any followers.</p>
    <ul class="list-group">
        <li class="list-group-item" ng-repeat="follower in ctrl.getUsers().data.content | orderBy:'-id'">
            <img ng-src="{{follower.imageUrl == null ? '/images/default-avatar.png' :
                    follower.imageUrl}}"
                 class="profile-photo-md">
            <a href="/#/profile/{{follower.id}}" ng-click="ctrl.cancel()" style="color: #444444;">
                {{follower.firstName}} {{follower.lastName}}
            </a>
            <div class="btn-group pull-right" ng-show="authenticated && principal.id == ctrl.getId()"
            ng-controller="FriendshipCtrl as friendshipCtrl">
                <a href="" class="btn btn-link" ng-click="friendshipCtrl.addFriend(follower)">Add to friends</a>
                <a href="" class="btn btn-link" ng-click="friendshipCtrl.openBlockUserModal(follower, 0)">Block</a>
            </div>
        </li>
    </ul>
</div>