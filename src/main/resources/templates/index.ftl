<!DOCTYPE html>
<html lang="en" ng-app="socialNetworkApp">
    <head>
        <title>${title}</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="theme-color" content="#6464ad">
        <link href="css/bootstrap.css" rel="stylesheet"/>
        <link href="css/app.css" rel="stylesheet"/>
        <link href="css/font-awesome.min.css" rel="stylesheet"/>
    </head>
    <body>
        <!-- NAVIGATION -->
        <nav class="navbar navbar-inverse">
            <div class="container" ng-init="isNavCollapsed = true" ng-controller="SecurityCtrl as ctrl">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" ng-click="isNavCollapsed = !isNavCollapsed">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="/">Social Network</a>
                </div>
                <div class="collapse navbar-collapse" uib-collapse="isNavCollapsed">
                    <ul class="nav navbar-nav navbar-right">
                        <li ui-sref-active="active" ng-show="authenticated">
                            <a ui-sref="profiles">
                                <i class="fa fa-search fa-lg fa-fw hidden-xs"></i>
                                <span class="hidden-sm hidden-md hidden-lg">Profiles</span>
                            </a>
                        </li>
                        <li ng-show="authenticated">
                            <a href="/#/news">
                                <i class="fa fa-newspaper-o fa-lg fa-fw hidden-xs"></i>
                                <span class="hidden-sm hidden-md hidden-lg">News</span>
                            </a>
                        </li>
                        <li ui-sref-active="active" ng-show="authenticated">
                            <a ui-sref="conversations">
                                <i class="fa fa-comments fa-lg fa-fw hidden-xs"></i>
                                <span class="hidden-sm hidden-md hidden-lg">Messages</span>
                                <span ng-show="ctrl.getUnopenedConversationsQuantity() > 0">
                                    +<span ng-bind="ctrl.getUnopenedConversationsQuantity()"></span>
                                </span>
                            </a>
                        </li>
                        <li ui-sref-active="active" ng-show="!authenticated">
                            <a ui-sref="register">Register</a>
                        </li>
                        <li ui-sref-active="active" ng-show="!authenticated">
                            <a ui-sref="login">Login</a>
                        </li>
                        <li class="dropdown" uib-dropdown ng-show="authenticated">
                            <a href="#" class="dropdown-toggle" uib-dropdown-toggle>
                                <span ng-bind="principal.firstName"></span>
                                <span ng-bind="principal.lastName"></span>
                                <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu" uib-dropdown-menu role="menu">
                                <li><a href="/#/profile/{{principal.id}}">Profile</a></li>
                                <li class="divider hidden-xs"></li>
                                <li><a href="/#/profile/{{principal.id}}/community">Communities</a></li>
                                <li><a href="/#/profile/principal/friends">Friends</a></li>
                                <li class="divider hidden-xs"></li>
                                <li><a href="" ng-click="ctrl.logout()">Logout</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <!-- MAIN CONTENT -->
        <div class="container">
            <div ui-view></div>
        </div>
        <!-- FOOTER -->
        <footer class="footer">
            <div class="container">
                <p class="text-muted pull-right">Social Network © 2017</p>
            </div>
        </footer>
        <!-- SCRIPTS (libs) -->
        <script src="js/lib/angular.min.js" ></script>
        <script src="js/lib/angular-animate.min.js" ></script>
        <script src="js/lib/angular-sanitize.min.js" ></script>
        <script src="js/lib/angular-ui-router.min.js" ></script>
        <script src="js/lib/datetime-picker.min.js"></script>
        <script src="js/lib/localforage.min.js" ></script>
        <script src="js/lib/ngStorage.min.js"></script>
        <script src="js/lib/sockjs.min.js"></script>
        <script src="js/lib/stomp.min.js"></script>
        <script src="js/lib/ui-bootstrap.min.js"></script>
        <!-- SCRIPTS (entry point) -->
        <script src="js/app/app.js"></script>
        <!-- SCRIPTS (services) -->
        <script src="js/app/common/services/chatService.js"></script>
        <script src="js/app/common/services/communityService.js"></script>
        <script src="js/app/common/services/conversationService.js"></script>
        <script src="js/app/common/services/fileService.js"></script>
        <script src="js/app/common/services/friendshipService.js"></script>
        <script src="js/app/common/services/messageService.js"></script>
        <script src="js/app/common/services/postService.js"></script>
        <script src="js/app/common/services/profileService.js"></script>
        <script src="js/app/common/services/userService.js"></script>
        <!-- SCRIPTS (controllers) -->
        <script src="js/app/common/controllers/BlockUserModalCtrl.js"></script>
        <script src="js/app/common/controllers/CommunitiesProfileViewModalCtrl.js"></script>
        <script src="js/app/common/controllers/CommunityCtrl.js"></script>
        <script src="js/app/common/controllers/CommunityListCtrl.js"></script>
        <script src="js/app/common/controllers/CommunityModalCtrl.js"></script>
        <script src="js/app/common/controllers/CommunitySettingsCtrl.js"></script>
        <script src="js/app/common/controllers/ConversationsCtrl.js"></script>
        <script src="js/app/common/controllers/FeedCtrl.js"></script>
        <script src="js/app/common/controllers/FriendshipCtrl.js"></script>
        <script src="js/app/common/controllers/PostEditModalCtrl.js"></script>
        <script src="js/app/common/controllers/ProfileCtrl.js"></script>
        <script src="js/app/common/controllers/ProfileEditModalCtrl.js"></script>
        <script src="js/app/common/controllers/ProfileFollowersModalCtrl.js"></script>
        <script src="js/app/common/controllers/ProfileFriendsModalCtrl.js"></script>
        <script src="js/app/common/controllers/SecurityCtrl.js"></script>
        <script src="js/app/common/controllers/UserCtrl.js"></script>
        <script src="js/app/common/controllers/ProfileMutualFriendsModalCtrl.js"></script>
        <script src="js/app/common/controllers/HomeCtrl.js"></script>
        <!-- SCRIPTS (directives) -->
        <script src="js/app/common/directives/fileDirective.js"></script>
        <script src="js/app/common/directives/passwordCheckDirective.js"></script>
        <script src="js/app/common/directives/scrollDirective.js"></script>
    </body>
</html>