<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" ng-click="ctrl.cancel()">×</button>
    <h4 class="modal-title">Friends</h4>
</div>
<div class="modal-body">
    <p class="lead" ng-show="ctrl.getUsers().data.content.length === 0 || !ctrl.getUsers()">The user does not have any friends.</p>
    <ul class="list-group">
        <li class="list-group-item" ng-repeat="friend in ctrl.getUsers().data.content | orderBy:'-id'">
            <img ng-src="{{friend.imageUrl == null ? '/images/default-avatar.png' :
                    friend.imageUrl}}"
                 class="profile-photo-md">
            <a href="/#/profile/{{friend.id}}" ng-click="ctrl.cancel()" style="color: #444444;">
                {{friend.firstName}} {{friend.lastName}}
            </a>
            <div class="btn-group pull-right" ng-show="authenticated && principal.id == ctrl.getId()"
                 ng-controller="FriendshipCtrl as friendshipCtrl">
                <a href="" class="btn btn-link" ng-click="friendshipCtrl.deleteFriend(friend)">Delete from friends</a>
                <a href="" class="btn btn-link" ng-click="friendshipCtrl.openBlockUserModal(friend, 0)">Block</a>
            </div>
        </li>
    </ul>
</div>