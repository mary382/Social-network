<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" ng-click="ctrl.cancel()">×</button>
    <h4 class="modal-title">Edit profile</h4>
</div>
<div class="modal-body">
    <form name="editProfileForm" novalidate>
        <div class="form-group"
             ng-class="{'has-error':editProfileForm.firstName.$dirty && editProfileForm.firstName.$invalid}">
            <label for="firstName">First name:</label>
            <input id="firstName" name="firstName" class="form-control" placeholder="First name" required
                   ng-pattern="ctrl.properNounPattern" ng-model="ctrl.profile.firstName">
            <span class="validation-tip"
                  ng-show="editProfileForm.firstName.$dirty && editProfileForm.firstName.$invalid">
                <span ng-show="editProfileForm.firstName.$error.required">First name is required.</span>
                <span ng-show="editProfileForm.firstName.$error.pattern">Invalid first name.</span>
            </span>
        </div>
        <div class="form-group"
             ng-class="{'has-error':editProfileForm.lastName.$dirty && editProfileForm.lastName.$invalid}">
            <label for="lastName">Last name:</label>
            <input id="lastName" name="lastName" class="form-control" placeholder="Last name" required
                   ng-pattern="ctrl.properNounPattern" ng-model="ctrl.profile.lastName">
            <span class="validation-tip" ng-show="editProfileForm.lastName.$dirty && editProfileForm.lastName.$invalid">
                <span ng-show="editProfileForm.lastName.$error.required">Last name is required.</span>
                <span ng-show="editProfileForm.lastName.$error.pattern">Invalid last name.</span>
            </span>
        </div>
        <div class="form-group">
            <label for="birthday">Birthday:</label>
            <p class="input-group">
                <input type="text" id="birthday" ng-model="ctrl.profile.birthday" placeholder="dd.MM.yyyy"
                       class="form-control" uib-datepicker-popup="dd.MM.yyyy"
                       datepicker-options="ctrl.datePicker.options"
                       is-open="ctrl.isOpen" enable-time="false" close-text="Close"/>
                <span class="input-group-btn">
                    <button type="button" class="btn btn-link" ng-click="ctrl.openCalendar($event)">
                        <i class="fa fa-calendar"></i>
                    </button>
                </span>
            </p>
        </div>
        <div class="form-group">
            <label for="country">Country:</label>
            <input id="country" class="form-control" placeholder="Country"
                   ng-model="ctrl.profile.country">
        </div>
        <div class="form-group">
            <label for="city">City:</label>
            <input id="city" class="form-control" placeholder="City"
                   ng-model="ctrl.profile.city">
        </div>
        <div class="form-group">
            <label for="resume">Resume:</label>
            <textarea id="resume" class="form-control" rows="2" placeholder="Resume"
                      ng-model="ctrl.profile.resume"></textarea>
        </div>
        <div class="form-group">
            <label for="resume">Avatar:</label>
            <div ng-show="ctrl.check() == 0">
                <img class="profile-photo-lg" ng-src="{{ctrl.profile.imageUrl}}"/>
                <button class="btn btn-link" ng-click="ctrl.deleteAvatar();">Delete</button>
            </div>
            <input ng-show="ctrl.check() == 1" type="file" accept="image/*" file-model="avatar"/>
        </div>
    </form>
</div>
<div class="modal-footer">
    <button type="button" class="btn btn-default" ng-click="ctrl.cancel()">Cancel</button>
    <button type="button" class="btn btn-primary"
            ng-click="ctrl.save();" ng-disabled="editProfileForm.$invalid">Save
    </button>
</div>