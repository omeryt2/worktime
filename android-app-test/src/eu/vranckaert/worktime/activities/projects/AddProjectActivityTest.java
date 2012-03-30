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

import android.view.View;
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
 * Date: 20/03/12
 * Time: 11:22
 */
public class AddProjectActivityTest extends ActivityTestCase<AddEditProjectActivity> {
    public AddProjectActivityTest() {
        super(AddEditProjectActivity.class);
    }

    @Override
    public void beforeTestInsertData(TimeRegistrationDao trDao, ProjectDao pDao, TaskDao tDao, CommentHistoryDao cDao) {
    }

    public void testValidationRequired() {
        // Check if by default the validation error are hidden
        View addProjectValidateRequired = solo.getView(R.id.projectname_required);
        View addProjectValidateUnique = solo.getView(R.id.projectname_unique);
        assertEquals("The validation error message 'required' should be gone", View.GONE, addProjectValidateRequired.getVisibility());
        assertEquals("The validation error message 'unique' should be gone", View.GONE, addProjectValidateUnique.getVisibility());

        // Try to add with empty values
        solo.clickOnImageButton(1);
        solo.assertCurrentActivity("The add/edit project activity is expected", AddEditProjectActivity.class);
        addProjectValidateRequired = solo.getView(R.id.projectname_required);
        addProjectValidateUnique = solo.getView(R.id.projectname_unique);
        assertEquals("The validation error message 'required' should be visible", View.VISIBLE, addProjectValidateRequired.getVisibility());
        assertEquals("The validation error message 'unique' should be gone", View.GONE, addProjectValidateUnique.getVisibility());
    }

    public void testValidationUnique() {
        // Check if by default the validation error are hidden
        View addProjectValidateRequired = solo.getView(R.id.projectname_required);
        View addProjectValidateUnique = solo.getView(R.id.projectname_unique);
        assertEquals("The validation error message 'required' should be gone", View.GONE, addProjectValidateRequired.getVisibility());
        assertEquals("The validation error message 'unique' should be gone", View.GONE, addProjectValidateUnique.getVisibility());

        // Enter the name of the default project that is already in use...
        EditText addProjectName = (EditText) solo.getView(R.id.projectname);
        solo.enterText(addProjectName, getActivity().getString(R.string.default_project_name));

        takeScreenshot();

        solo.clickOnImageButton(1);
        solo.assertCurrentActivity("The add/edit project activity is expected", AddEditProjectActivity.class);
        addProjectValidateRequired = solo.getView(R.id.projectname_required);
        addProjectValidateUnique = solo.getView(R.id.projectname_unique);
        assertEquals("The validation error message 'required' should be gone", View.GONE, addProjectValidateRequired.getVisibility());
        assertEquals("The validation error message 'unique' should be visible", View.VISIBLE, addProjectValidateUnique.getVisibility());
    }

    public void testAddValidProject() {
        // Check if by default the validation error are hidden
        View addProjectValidateRequired = solo.getView(R.id.projectname_required);
        View addProjectValidateUnique = solo.getView(R.id.projectname_unique);
        assertEquals("The validation error message 'required' should be gone", View.GONE, addProjectValidateRequired.getVisibility());
        assertEquals("The validation error message 'unique' should be gone", View.GONE, addProjectValidateUnique.getVisibility());

        // Enter a project name and save
        EditText addProjectName = (EditText) solo.getView(R.id.projectname);
        solo.enterText(addProjectName, "A custom project name");
        solo.clickOnImageButton(1);
        solo.waitForDialogToClose(TestUtil.Time.FIVE_SECONDS);
        assertEquals("As the activity should be ended no views are expected to be found!", 0, solo.getCurrentViews().size());
    }
}
