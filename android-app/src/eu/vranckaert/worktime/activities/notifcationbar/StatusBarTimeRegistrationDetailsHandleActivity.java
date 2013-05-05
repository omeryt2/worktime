package eu.vranckaert.worktime.activities.notifcationbar;

import android.content.Intent;
import android.os.Bundle;
import com.google.inject.Inject;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.ProjectService;
import eu.vranckaert.worktime.service.TaskService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import roboguice.activity.RoboActivity;

/**
 * Date: 3/05/13
 * Time: 12:39
 *
 * @author Dirk Vranckaert
 */
public class StatusBarTimeRegistrationDetailsHandleActivity extends RoboActivity {
    @Inject
    private TimeRegistrationService timeRegistrationService;

    @Inject
    private TaskService taskService;

    @Inject
    private ProjectService projectService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launchTimeRegistrationActionsDialog();
    }

    private void launchTimeRegistrationActionsDialog() {
        TimeRegistration timeRegistration = timeRegistrationService.getLatestTimeRegistration();
        taskService.refresh(timeRegistration.getTask());
        projectService.refresh(timeRegistration.getTask().getProject());

        TimeRegistration timeRegistrationPrevious = timeRegistrationService.getPreviousTimeRegistration(timeRegistration);
        TimeRegistration timeRegistrationNext = timeRegistrationService.getNextTimeRegistration(timeRegistration);

        if (timeRegistration != null) {
            Intent intent = new Intent();
            intent.setAction(Constants.Broadcast.TIME_REGISTRATION_DETAILS);
            intent.putExtra(Constants.Extras.TIME_REGISTRATION, timeRegistration);
            intent.putExtra(Constants.Extras.TIME_REGISTRATION_PREVIOUS, timeRegistrationPrevious);
            intent.putExtra(Constants.Extras.TIME_REGISTRATION_NEXT, timeRegistrationNext);
            sendBroadcast(intent);
        }
        finish();
    }
}
