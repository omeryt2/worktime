/*
 * Copyright 2012 Dirk Vranckaert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.vranckaert.worktime.activities.projects;

import android.app.ListActivity;
import android.widget.EditText;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.dao.CommentHistoryDao;
import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.test.cases.ActivityTestCase;
import eu.vranckaert.worktime.test.utils.TestUtil;

/**
 * User: DIRK VRANCKAERT
 * Date: 20/01/12
 * Time: 9:54
 */
public class ManageProjectsActivityTest extends ActivityTestCase<ManageProjectsActivity> {
    private String newProjectName = "WorkTime for Android";
    private String editedProjectName = "EDIT_PROJECT_!";
    
    public ManageProjectsActivityTest() {
        super(ManageProjectsActivity.class);
    }

    @Override
    public void beforeTestInsertData(TimeRegistrationDao trDao, ProjectDao pDao, TaskDao tDao, CommentHistoryDao cDao) {
    }

    public void testDefaults() {
        int projectCount = ((ListActivity)solo.getCurrentActivity()).getListView().getAdapter().getCount();
        assertEquals("Initially we expect exactly one project to be available", 1, projectCount);

        solo.clickLongInList(0);
        solo.waitForDialogToClose(TestUtil.Time.SIXTY_SECONDS);
        assertTrue(solo.searchText(getActivity().getString(R.string.lbl_projects_menu_details), 1, true));
        assertTrue(solo.searchText(getActivity().getString(R.string.lbl_projects_menu_edit), 1, true));
        assertTrue(solo.searchText(getActivity().getString(R.string.lbl_projects_menu_copy), 1, true));
        assertTrue(solo.searchText(getActivity().getString(R.string.lbl_projects_menu_add), 1, true));
    }

    private void addProject(String projectName) {
        // Click the add button
        solo.clickOnImageButton(2);
        solo.waitForActivity(AddEditProjectActivity.class.getSimpleName());
        solo.waitForDialogToClose(TestUtil.Time.TWENTY_SECONDS);
        solo.assertCurrentActivity("The add/edit project activity is expected", AddEditProjectActivity.class);

        takeScreenshot();

        // Enter a new (exclusive) project name
        EditText addProjectName = (EditText) solo.getView(R.id.projectname);
        solo.enterText(addProjectName, projectName);

        takeScreenshot();

        // Save the new project
        solo.clickOnImageButton(1);
        solo.waitForActivity(ManageProjectsActivity.class.getSimpleName());
        solo.waitForDialogToClose(TestUtil.Time.TWENTY_SECONDS);
        solo.assertCurrentActivity("The projects activity is expected", ManageProjectsActivity.class);
    }

    public void testAddProject() {
        // Check that a project with this name does not exist already
        assertFalse("The project with name " + newProjectName + " should not yet be available!", solo.searchText(newProjectName));

        int projectCount = ((ListActivity)solo.getCurrentActivity()).getListView().getAdapter().getCount();
        assertEquals("Initially we expect exactly one project to be available", 1, projectCount);

        // Add a new project
        addProject(newProjectName);

        // Search for the project name on the list activity
        assertTrue("The project with name " + newProjectName + " should be available!", solo.searchText(newProjectName));

        projectCount = ((ListActivity)solo.getCurrentActivity()).getListView().getAdapter().getCount();
        assertEquals("Two projects should be available now", 2, projectCount);
    }

    public void testEditProject() {
        // Add a new project
        addProject(newProjectName);

        takeScreenshot();

        // Search for the project name on the list activity
        assertTrue("The project with name " + newProjectName + " should be available!", solo.searchText(newProjectName));

        int projectCount = ((ListActivity)solo.getCurrentActivity()).getListView().getAdapter().getCount();
        assertEquals("Two projects should be available", 2, projectCount);
        
        // Edit the project
        solo.clickLongOnText(newProjectName);
        solo.waitForDialogToClose(TestUtil.Time.THIRTY_SECONDS);
        solo.clickOnText(getActivity().getString(R.string.lbl_projects_menu_edit));
        solo.waitForActivity(AddEditProjectActivity.class.getSimpleName());
        solo.waitForDialogToClose(TestUtil.Time.TWENTY_SECONDS);
        solo.assertCurrentActivity("The add/edit project activity is expected", AddEditProjectActivity.class);

        takeScreenshot();
        
        // Check that the project name is correct
        EditText editProjectName = (EditText) solo.getView(R.id.projectname);
        assertEquals(newProjectName, editProjectName.getText().toString());

        // Change the project name
        solo.clearEditText(editProjectName);
        solo.enterText(editProjectName, editedProjectName);

        takeScreenshot();

        // Update the project
        solo.clickOnImageButton(1);
        solo.waitForActivity(ManageProjectsActivity.class.getSimpleName());
        solo.waitForDialogToClose(TestUtil.Time.TWENTY_SECONDS);
        solo.assertCurrentActivity("The projects activity is expected", ManageProjectsActivity.class);

        // Search for the project name on the list activity
        assertTrue("The project with name " + editedProjectName + " should be available!", solo.searchText(editedProjectName));

        projectCount = ((ListActivity)solo.getCurrentActivity()).getListView().getAdapter().getCount();
        assertEquals("Two projects should still be available", 2, projectCount);
    }
}
