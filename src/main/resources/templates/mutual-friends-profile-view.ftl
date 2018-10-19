<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" ng-click="ctrl.cancel()">×</button>
    <h4 class="modal-title">Mutual friends</h4>
</div>
<div class="modal-body">
    <p class="lead" ng-show="!ctrl.getUsers()">There are no mutual Friends.</p>
    <ul class="list-group">
        <li class="list-group-item" ng-repeat="friend in ctrl.getUsers().data.content | orderBy:'-id'">
            <img ng-src="{{friend.imageUrl == null ? '/images/default-avatar.png' :
                    friend.imageUrl}}"
                 class="profile-photo-md">
            <a href="/#/profile/{{friend.id}}" ng-click="ctrl.cancel()" style="color: #444444;">
                {{friend.firstName}} {{friend.lastName}}
            </a>
        </li>
    </ul>
</div>