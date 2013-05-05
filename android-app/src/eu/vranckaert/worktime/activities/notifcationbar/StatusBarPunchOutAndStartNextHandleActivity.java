package eu.vranckaert.worktime.activities.notifcationbar;

import eu.vranckaert.worktime.enums.timeregistration.TimeRegistrationAction;

/**
 * Date: 3/05/13
 * Time: 15:20
 *
 * @author Dirk Vranckaert
 */
public class StatusBarPunchOutAndStartNextHandleActivity extends StatusBarActionDialogActivity {
    @Override
    protected TimeRegistrationAction getTimeRegistrationAction() {
        return TimeRegistrationAction.PUNCH_OUT_AND_START_NEXT;
    }
}
