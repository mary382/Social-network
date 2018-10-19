<div class="generic-container">
    <div class="col-md-5">
        <div class="panel panel-default">
            <div class="panel-body" ng-init="ctrl.initCommunity()">
                <div ng-show="ctrl.community.logoUrl != null">
                    <img width="300" height="300" src="{{ctrl.community.logoUrl}}">
                </div>
                <div>
                    <span class="lead"><h2>{{ctrl.community.title}}</h2></span>
                </div>
                <hr>
                <div ng-show="ctrl.community.info != null">
                    <span class="text-muted">Info:</span> {{ctrl.community.info}}
                </div>
                <div>
                    <a ng-click="ctrl.openParticipantsModal()">Followers</a>
                    {{ctrl.community.participantsCount | number}}
                </div>
            </div>
        </div>
        <div ng-show="authenticated && principal.id == ctrl.community.owner.id">
            <input type="file" accept="image/*" file-model="logotype"/>
            <div class="btn-group btn-group-justified">
                <a class="btn btn-success" ng-click="ctrl.uploadLogotype()">Upload Logotype</a>
                <a class="btn btn-warning" href="/#/community/{{ctrl.community.id}}/settings">Settings</a>
            </div>
        </div>
        <div class="btn-group btn-group-justified" ng-show="authenticated && principal.id != ctrl.community.owner.id">
            <a class="btn btn-primary" ng-click="ctrl.commParticipantFlag ? ctrl.unfollowCommunity() : ctrl.followCommunity()">
                {{ctrl.commParticipantFlag ? 'Unfollow' : 'Follow'}}
            </a>
        </div>
        <br>
    </div>
    <div class="col-md-7">
        <div class="panel panel-default" ng-show="authenticated && principal.id == ctrl.community.owner.id">
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
        <p class="lead text-center" ng-show="ctrl.totalPosts == 0">The community does not have any posts.</p>
    <div class="panel panel-default" ng-class="{'post-anim':$first}"
         ng-repeat="post in ctrl.posts.content | orderBy : '-time'">
        <div class="panel-body">
            <div class="dropdown" uib-dropdown>
                <a href="/#/profile/{{post.author.id}}">{{post.author.firstName}} {{post.author.lastName}}</a>
                <span class="text-muted">posted on {{post.time | date: "MMMM d 'at' HH:mm"}}</span>
                <a href="" data-target="#" data-toggle="dropdown" class="dropdown-toggle pull-right"
                   uib-dropdown-toggle ng-show="authenticated && principal.id == ctrl.community.owner.id">
                    <i class="material-icons">more_horiz</i>
                </a>
                <ul class="dropdown-menu pull-right" uib-dropdown-menu>
                    <li><a href="" ng-click="ctrl.openUpdatePostModal(post)">Update</a></li>
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