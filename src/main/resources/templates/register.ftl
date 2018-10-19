<div class="generic-container">
    <div class="col-md-8 col-md-offset-2">
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="lead">Register</span>
            </div>
            <div class="panel-body">
                <div class="alert alert-success" role="alert" ng-if="ctrl.successMessage">
                    {{ctrl.successMessage}}
                </div>
                <div class="alert alert-danger" role="alert" ng-if="ctrl.errorMessage">
                    {{ctrl.errorMessage}}
                </div>
                <form name="registerForm" ng-submit="ctrl.register()" novalidate>
                    <div class="form-group"
                         ng-class="{'has-error':registerForm.firstName.$dirty && registerForm.firstName.$invalid}">
                        <label for="firstName">First name:</label>
                        <input id="firstName" name="firstName" class="form-control" placeholder="First name"
                               required autofocus ng-pattern="ctrl.properNounPattern" ng-model="ctrl.user.firstName">
                        <span class="validation-tip" ng-show="registerForm.firstName.$dirty && registerForm.firstName.$invalid">
                            <span ng-show="registerForm.firstName.$error.required">First name is required.</span>
                            <span ng-show="registerForm.firstName.$error.pattern">Invalid first name.</span>
                        </span>
                    </div>
                    <div class="form-group"
                         ng-class="{'has-error':registerForm.lastName.$dirty && registerForm.lastName.$invalid}">
                        <label for="lastName">Last name:</label>
                        <input id="lastName" name="lastName" class="form-control" placeholder="Last name"
                               required ng-pattern="ctrl.properNounPattern" ng-model="ctrl.user.lastName">
                        <span class="validation-tip" ng-show="registerForm.lastName.$dirty && registerForm.lastName.$invalid">
                            <span ng-show="registerForm.lastName.$error.required">Last name is required.</span>
                            <span ng-show="registerForm.lastName.$error.pattern">Invalid last name.</span>
                        </span>
                    </div>
                    <div class="form-group"
                         ng-class="{'has-error':registerForm.email.$dirty && registerForm.email.$invalid}">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" class="form-control" placeholder="Email"
                               required ng-pattern="ctrl.emailPattern" ng-model="ctrl.user.email">
                        <span class="validation-tip" ng-show="registerForm.email.$dirty && registerForm.email.$invalid">
                            <span ng-show="registerForm.email.$error.required">Email is required.</span>
                            <span ng-show="registerForm.email.$error.pattern">Invalid email address.</span>
                        </span>
                    </div>
                    <div class="form-group"
                         ng-class="{'has-error':registerForm.password.$dirty && registerForm.password.$invalid}">
                        <label for="password">Password <span class="text-muted">(must be 6-60 characters long and include both letters and numbers)</span>:</label>
                        <input type="password" id="password" name="password" class="form-control" placeholder="Password"
                               required ng-pattern="ctrl.passwordPattern" ng-model="ctrl.user.password">
                        <span class="validation-tip" ng-show="registerForm.password.$dirty && registerForm.password.$invalid">
                            <span ng-show="registerForm.password.$error.required">Password is required.</span>
                            <span ng-show="registerForm.password.$error.pattern">Invalid password.</span>
                        </span>
                    </div>
                    <div class="form-group"
                         ng-class="{'has-error':registerForm.passwordConfirm.$dirty && registerForm.passwordConfirm.$invalid}">
                        <label for="password">Confirm password <span class="text-muted">(the same password as in the previous field)</span>:</label>
                        <input type="password" id="passwordConfirm" name="passwordConfirm" class="form-control"
                               placeholder="Password confirmation" required ng-model="passwordConfirm"
                               password-confirm match-target="ctrl.user.password">
                        <span class="validation-tip" ng-show="registerForm.passwordConfirm.$dirty && registerForm.passwordConfirm.$error">
                            <span ng-show="registerForm.passwordConfirm.$error.mismatch">Passwords do not match.</span>
                        </span>
                    </div>
                    <div class="form-actions pull-right">
                        <input type="submit" value="Register" class="btn btn-primary btn-lg"
                               ng-disabled="registerForm.$invalid">
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>