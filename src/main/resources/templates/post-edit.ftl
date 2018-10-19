<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" ng-click="ctrl.cancel()">×</button>
    <h4 class="modal-title">Update post</h4>
</div>
<div class="modal-body">
    <form name="editPostForm" novalidate>
        <div class="form-group">
            <label for="post">Post:</label>
            <textarea id="post" class="form-control" rows="3" placeholder="Write something..." required
                      ng-model="ctrl.post.text"></textarea>
            <input type="file" file-model="attachments" multiple/>
            <ul>
                <li ng-repeat="file in attachments">{{file.name}}</li>
            </ul>
            <div ng-repeat="attachment in ctrl.post.attachments">
                <div ng-show="attachment.type == 'IMAGE'">
                    <img width="300" src="{{attachment.url}}">
                    <button class="btn btn-link" ng-click="ctrl.remove(attachment);">Delete</button>
                </div>
                <div ng-show="attachment.type == 'AUDIO'">
                    <audio controls preload="none">
                        <source src="{{ctrl.trustSrc(attachment.url)}}" type="audio/mpeg"/>
                    </audio>
                    <button class="btn btn-link" ng-click="ctrl.remove(attachment);">Delete</button>
                </div>
                <div ng-show="attachment.type == 'DOCUMENT'">
                    <a href="{{attachment.url}}" download="">{{attachment.title}}</a>
                    <button class="btn btn-link" ng-click="ctrl.remove(attachment);">Delete</button>
                </div>
                <div ng-show="attachment.type == 'VIDEO'">
                    <video width="300" height="300" poster="https://static.123apps.com/i/glyphs/webcamera.svg"
                           controls preload="none">
                        <source src="{{ctrl.trustSrc(attachment.url)}}" type="video/mp4">
                    </video>
                    <button class="btn btn-link" ng-click="ctrl.remove(attachment);">Delete</button>
                </div>
            </div>
        </div>
    </form>
</div>
<div class="modal-footer">
    <button type="button" class="btn btn-default" ng-click="ctrl.cancel()">Cancel</button>
    <button type="button" class="btn btn-primary"
            ng-click="ctrl.save();" ng-disabled="editPostForm.$invalid">Update</button>
</div>