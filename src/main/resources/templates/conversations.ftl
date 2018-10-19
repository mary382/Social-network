<div class="generic-container" ng-init="ctrl.initConversation()">
    <div class="col-md-4 full-size-container">
        <div class="panel panel-default inner-content">
            <div class="panel-body">
                <form class="conversations-search-form">
                    <div class="form-group">
                        <input class="form-control" placeholder="Search conversations" ng-model="searchText">
                    </div>
                </form>
                <div class="list-group">
                    <a href="" class="list-group-item list-item-anim"
                       ng-class="{'active':conversation.id===ctrl.currentConversation.id}"
                       ng-click="ctrl.switchConversation(conversation)"
                       ng-repeat="conversation in ctrl.getAllConversations()
                       | orderBy:'-lastModified' | filter:searchText track by conversation.id">
                        <span ng-repeat="participant in conversation.participants | filter:{id:'!'+principal.id}">
                            {{participant.firstName}} {{participant.lastName}}
                        </span>
                        <span class="badge" ng-repeat="(key, value) in conversation.notificationsQuantity"
                              ng-show="key==principal.id && value!=0">{{value}}</span>
                    </a>
                </div>
            </div>
        </div>
    </div>
    <div class="visible-xs visible-sm">
        <br>
    </div>
    <div class="col-md-8">
        <div class="text-center" ng-show="!ctrl.currentConversation.id">
            <br>
            <span class="lead">Select a conversation from the list.</span>
        </div>
        <div class="panel panel-default conversation-panel" ng-show="ctrl.currentConversation.id">
            <div class="panel-heading">
                <span class="lead">
                    <span ng-repeat="participant in ctrl.currentConversation.participants | filter:{id:'!'+principal.id}">
                        <a href="/#/profile/{{participant.id}}" target="blank">
                            {{participant.firstName}} {{participant.lastName}}
                        </a>
                    </span>
                    <button type="button" class="close" ng-click="ctrl.currentConversation = {}">×</button>
                </span>
            </div>
            <div class="panel-body">
                <div class="conversation-box" scroll="ctrl.getMessages()">
                    <div class="chat-message-wrapper" ng-show="!ctrl.isLastPage()">
                        <a class="btn btn-link btn-block" ng-click="ctrl.loadMoreMessages()">
                            Load more messages
                        </a>
                    </div>
                    <div class="chat-message-wrapper"
                         ng-repeat="message in ctrl.getMessages() | orderBy:'time' track by $index"
                         ng-class="{'chat-message-right':message.author.id === principal.id}">
                        <div class="chat-user-avatar">
                            <a href="/#/profile/{{message.author.id}}" target="blank">
                                <img ng-src="{{message.author.imageUrl === null ?
                                '/images/default-avatar.png' : message.author.imageUrl}}"
                                     title="{{message.author.firstName}} {{message.author.lastName}}"
                                     class="profile-photo-md">
                            </a>
                        </div>
                        <ul class="chat-message">
                            <li>
                                <p>
                                    {{message.text}}
                                    <span class="chat-message-time">
                                            {{message.time | date: ctrl.chooseDateFormat(message.time)}}
                                    </span>
                                </p>
                            </li>
                        </ul>
                    </div>
                </div>
                <div>
                    <form name="newMessageForm" ng-submit="ctrl.sendMessage()">
                        <div class="form-group">
                            <textarea id="newMessage" class="form-control" rows="2"
                                      placeholder="Write a message..." required
                                      ng-model="ctrl.newMessage.text"></textarea>
                        </div>
                        <div class="form-actions">
                            <div class="blocked-tip validation-tip fade-in-anim fade-out-anim" ng-show="ctrl.isBlocked">
                                The user has blocked you. You can still send messages, but the user will not see them
                                until unblock you.
                            </div>
                            <a class="btn btn-primary pull-right" ng-click="ctrl.sendMessage()"
                               ng-disabled="newMessageForm.$invalid">
                                <i class="fa fa-paper-plane"></i>
                                Send
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>