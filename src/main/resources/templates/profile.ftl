<div class="generic-container">
    <div ng-controller="FriendshipCtrl as friendshipCtrl">
    <div class="col-md-5">
        <div class="panel panel-default">
            <div class="panel-body" ng-init="ctrl.initProfile()">
                <div class="text-center">
                    <img ng-src="{{ctrl.profile.imageUrl == null ? '/images/default-avatar.png' :
                    ctrl.profile.imageUrl}}"
                         class="profile-photo-lg"/>
                </div>
                <div class="profile-name text-center">
                    <span class="lead profile-name">{{ctrl.profile.firstName}} {{ctrl.profile.lastName}}</span>
                </div>
                <div class="btn-group btn-group-justified" ng-show="authenticated && principal.id == ctrl.profile.id">
                    <a href="" class="btn btn-link" ng-click="ctrl.openEditModal()">Edit profile</a>
                </div>
                <hr>
                <div>
                    <span class="text-muted">Birthday:</span> {{(ctrl.profile.birthday | date: "dd.MM.yyyy") || "-" }}
                </div>
                <div>
                    <span class="text-muted">Location:</span> {{ctrl.profile.city || "-"}}, {{ctrl.profile.country ||
                    "-"}}
                </div>
                <div>
                    <span class="text-muted">Email:</span> {{ctrl.profile.email}}
                </div>
                <div ng-show="ctrl.profile.resume">
                    <hr>
                    {{ctrl.profile.resume}}
                </div>
                <hr>
                <div class="btn-group btn-group-justified" style="padding-top: 10px;"
                     ng-show="friendshipCtrl.getFriendshipStatus() !== 5 && friendshipCtrl.getFriendshipStatus() !== 6">
                    <a href="" ng-click="ctrl.openFriendsProfileViewModal(ctrl.profile.id);"
                       class="btn btn-link">Friends</a>
                    <a href="" ng-click="ctrl.openProfileFollowersModal(ctrl.profile.id);"
                       class="btn btn-link">Followers</a>
                    <a href="" ng-click="ctrl.openMutualFriendsProfileViewModal(principal.id, ctrl.profile.id);"
                       class="btn btn-link" ng-show="principal.id != ctrl.profile.id">Mutual friends</a>
                    <a href="" ng-click="ctrl.openCommunitiesProfileViewModal(ctrl.profile.id);"
                       class="btn btn-link">Communities</a>
                </div>
            </div>
        </div>
        <div class="text-center" ng-show="authenticated && principal.id != ctrl.profile.id"
             ng-controller="FriendshipCtrl as friendshipCtrl">
            <div class="btn-group" uib-dropdown>
                <a class="btn btn-primary" ng-click="ctrl.createConversation()">Send message</a>
                <button id="single-button" type="button" class="btn btn-default" uib-dropdown-toggle
                        ng-disabled="disabled">
                    {{friendshipCtrl.getStatusMessage()}} <span class="caret"></span>
                </button>
                <ul class="dropdown-menu pull-right" uib-dropdown-menu>
                    <li><a class="btn btn-default" ng-show="friendshipCtrl.getFriendshipStatus() == 2"
                           ng-click="friendshipCtrl.addFriend(ctrl.profile)">Approve incoming request</a></li>
                    <li><a class="btn btn-default" ng-show="friendshipCtrl.getFriendshipStatus() == 2"
                           ng-click="friendshipCtrl.deleteFriend(ctrl.profile)">Decline incoming request</a></li>
                    <li><a class="btn btn-default" ng-show="friendshipCtrl.getFriendshipStatus() == 0"
                           ng-click="friendshipCtrl.addFriend(ctrl.profile)">Send friend request</a></li>
                    <li><a class="btn btn-default" ng-show="friendshipCtrl.getFriendshipStatus() == 1"
                           ng-click="friendshipCtrl.deleteFriend(ctrl.profile)">Delete from friends</a></li>
                    <li><a class="btn btn-default" ng-show="friendshipCtrl.getFriendshipStatus() == 3"
                           ng-click="friendshipCtrl.deleteFriend(ctrl.profile)">Decline Outgoing request</a></li>
                    <li><a class="btn btn-default" ng-show="friendshipCtrl.getFriendshipStatus() == 4 ||
                    friendshipCtrl.getFriendshipStatus() == 6"
                           ng-click="friendshipCtrl.unblockUser(ctrl.profile)">Unblock {{ctrl.profile.firstName}}</a>
                    </li>
                    <li><a class="btn btn-default" ng-click="friendshipCtrl.openBlockUserModal(ctrl.profile, 0)">Block
                        {{ctrl.profile.firstName}}</a></li>
                </ul>

            </div>
        </div>
        <br>
        <br>
    </div>
    <p class="lead text-center"
       ng-show="friendshipCtrl.getFriendshipStatus() == 5 || friendshipCtrl.getFriendshipStatus() == 6">The user has
        restricted access to his page.</p>
    <div class="col-md-7"
         ng-show="friendshipCtrl.getFriendshipStatus() !== 5 && friendshipCtrl.getFriendshipStatus() !== 6">
        <div class="panel panel-default" ng-show="authenticated">
            <div class="panel-body">
                <form name="newPostForm" ng-submit="ctrl.createPost()">
                    <div class="form-group">
                        <label for="post">New post:</label>
                        <textarea id="post" class="form-control" rows="3" placeholder="Write something..." required
                                  ng-model="ctrl.newPost.text"></textarea>
                        <input type="file" file-model="attachments" multiple/>
                        <ul>
                            <li ng-repeat="file in attachments">{{file.name}}</li>
                        </ul>
                    </div>
                    <div class="form-actions pull-right">
                        <input type="submit" value="Submit" class="btn btn-primary"
                               ng-disabled="newPostForm.$invalid">
                    </div>
                </form>
            </div>
        </div>
        <p class="lead text-center" ng-show="ctrl.totalPosts == 0">The user does not have any posts.</p>
        <div class="panel panel-default" ng-class="{'post-anim':$first}"
             ng-repeat="post in ctrl.posts.content | orderBy : '-time'">
            <div class="panel-body">
                <div class="dropdown" uib-dropdown>
                    <a href="/#/profile/{{post.author.id}}">{{post.author.firstName}} {{post.author.lastName}}</a>
                    <span class="text-muted">posted on {{post.time | date: "MMMM d 'at' HH:mm"}}</span>
                    <a href="" data-target="#" data-toggle="dropdown" class="dropdown-toggle pull-right"
                       uib-dropdown-toggle ng-show="authenticated && principal.id == post.ownerId ||
                       principal.id == post.author.id">
                        <i class="fa fa-ellipsis-v fa-fw fa-lg"></i>
                    </a>
                    <ul class="dropdown-menu pull-right" uib-dropdown-menu>
                        <li><a href="" ng-click="ctrl.openUpdatePostModal(post)"
                               ng-show="authenticated && principal.id == post.author.id">Update</a></li>
                        <li><a href="" ng-click="ctrl.deletePost(post.id)">Delete</a></li>
                    </ul>
                </div>
                <p>{{post.text}}</p>
                <div ng-repeat="attachment in post.attachments">
                    <div ng-show="attachment.type == 'IMAGE'">
                        <img width="300" src="{{attachment.url}}">
                    </div>
                    <div ng-show="attachment.type == 'AUDIO'">
                        <audio controls preload="none">
                            <source src="{{ctrl.trustSrc(attachment.url)}}" type="audio/mpeg"/>
                        </audio>
                    </div>
                    <div ng-show="attachment.type == 'DOCUMENT'">
                        <a href="{{attachment.url}}" download="">{{attachment.title}}</a>
                    </div>
                    <div ng-show="attachment.type == 'VIDEO'">
                        <video width="300" height="300" poster="https://static.123apps.com/i/glyphs/webcamera.svg"
                               controls preload="none">
                            <source src="{{ctrl.trustSrc(attachment.url)}}" type="video/mp4">
                        </video>
                    </div>
                </div>
            </div>
        </div>
        <ul uib-pagination total-items="ctrl.totalPosts" ng-model="ctrl.currentPage" ng-show="ctrl.totalPosts != 0"
            ng-change="ctrl.postPageChanged()" class="pagination-sm" items-per-page="ctrl.postsPerPage"></ul>
    </div>
    </div>
</div>