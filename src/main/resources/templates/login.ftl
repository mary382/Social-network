<div class="generic-container">
    <div class="col-md-6 col-md-offset-3">
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="lead">Login</span>
            </div>
            <div class="panel-body">
                <div class="alert alert-danger" ng-show="ctrl.error">
                    There was a problem logging in. Please try again.
                </div>
                <form ng-submit="ctrl.login()" novalidate>
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input id="email" type="email" name="email" class="form-control" placeholder="Email"
                               autofocus ng-model="ctrl.credentials.username"/>
                    </div>
                    <div class="form-group">
                        <label for="password">Password:</label>
                        <input id="password" type="password" name="password" class="form-control" placeholder="Password"
                               ng-model="ctrl.credentials.password"/>
                    </div>
                    <div class="form-actions pull-right">
                        <button class="btn btn-primary btn-lg">Login</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>