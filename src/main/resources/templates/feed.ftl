<div class="generic-container">
    <div class="col-md-6 col-md-offset-3">
        <div class="panel panel-default" ng-show="authenticated">
            <div class="panel-body">
                <form name="newPostForm" ng-submit="ctrl.createPost()">
                    <div class="form-group">
                        <label for="post">New post:</label>
                        <textarea id="post" class="form-control" rows="3" placeholder="Write something..." required
                                  ng-model="ctrl.post.text"></textarea>
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
        <div class="btn-group btn-group-justified" ng-show="ctrl.currentPage==1 && ctrl.somethingNew">
            <a class="btn btn-default" ng-click="ctrl.updateNews()">Show {{ctrl.count}} new post</a>
        </div>
        <p class="lead text-center" ng-show="ctrl.totalElements == 0">There are no posts.</p>
        <div class="panel panel-default" ng-class="{'post-anim':$first}"
             ng-repeat="post in ctrl.posts.content | orderBy : '-time'">
            <div class="panel-body">
                <p>
                    <a ng-show="post.ownerId < 0" href="/#/community/{{post.ownerId * -1 }}">Community</a>
                    <a ng-show="post.ownerId > 0" href="/#/profile/{{post.author.id}}">{{post.author.firstName}} {{post.author.lastName}}</a>
                    <span class="text-muted">posted on {{post.time | date: "MMMM d 'at' HH:mm"}}</span>
                </p>
                <p>{{post.text}}</p>
                <div ng-repeat="attachment in post.attachments">
                    <div ng-show="attachment.type == 'IMAGE'">
                        <img width="500" height="300" src="{{attachment.url}}">
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
        <ul uib-pagination ng-show="ctrl.totalElements != 0" total-items="ctrl.totalElements" ng-model="ctrl.currentPage"
            ng-change="ctrl.pageChanged()" class="pagination-sm" items-per-page="ctrl.itemsPerPage"></ul>
    </div>
</div>