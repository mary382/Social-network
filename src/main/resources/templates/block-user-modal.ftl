<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" ng-click="ctrl.cancel()">×</button>
    <h4 class="modal-title">Block user</h4>
</div>
<div class="modal-body">
    <form name="blockUserForm" novalidate>
        <div class="btn-group">
            <label class="btn btn-primary" ng-model="ctrl.radioModel" uib-btn-radio="'Permanently'">Permanently</label>
            <label class="btn btn-primary" ng-model="ctrl.radioModel" uib-btn-radio="'Temporarily'">Temporarily</label>
        </div>
        <div class="form-group" ng-show="ctrl.radioModel == 'Temporarily'">
            <label for="date">Check date and time for unblock user:</label>
            <p class="input-group">
                <input type="text" id="date" ng-model="ctrl.time" placeholder="dd.MM.yyyy HH:mm"
                       class="form-control" datetime-picker="dd.MM.yyyy HH:mm"
                       datepicker-options="ctrl.datePicker.options"
                       is-open="ctrl.isOpen" close-text="Close"/>
                <span class="input-group-btn">
                <button type="button" class="btn btn-default" ng-click="ctrl.openCalendar($event)">
                    <i class="fa fa-calendar"></i></button>
            </span>
            </p>
        </div>
    </form>
</div>
<div class="modal-footer">
    <button type="button" class="btn btn-default" ng-click="ctrl.cancel()">Cancel</button>
    <button type="button" class="btn btn-primary"
            ng-click="ctrl.block();">Block
    </button>
</div>