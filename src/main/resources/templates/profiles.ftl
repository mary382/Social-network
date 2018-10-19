<div class="generic-container">
    <div class="col-md-6 col-md-offset-3">
        <form class="form-search">
            <div class="form-group">
                <input class="form-control input-lg" placeholder="Search profiles" ng-model="searchText">
            </div>
            <br>
            <div class="panel panel-default profile-anim" ng-repeat="user in ctrl.getAllProfiles() | filter:searchText">
                <div class="panel-body">
                    <div>
                    <span class="lead">
                        <a href="/#/profile/{{user.id}}">{{user.firstName}} {{user.lastName}}</a>
                    </span>
                    </div>
                    <div>
                        <span class="text-muted">Birthday:</span> {{(user.birthday | date: "dd.MM.yyyy") || "-"}}
                    </div>
                    <div>
                        <span class="text-muted">Location:</span> {{user.city || "-"}}, {{user.country || "-"}}
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>