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

package eu.vranckaert.worktime.activities.timeregistrations;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.service.CommentHistoryService;
import eu.vranckaert.worktime.service.TimeRegistrationService;
import eu.vranckaert.worktime.utils.context.ContextUtils;
import eu.vranckaert.worktime.utils.string.StringUtils;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;

/**
 * User: DIRK VRANCKAERT
 * Date: 27/04/11
 * Time: 23:27
 */
public class AddEditTimeRegistrationsComment extends GuiceActivity {
    private static final String LOG_TAG = AddEditTimeRegistrationsComment.class.getSimpleName();

    @InjectExtra(Constants.Extras.TIME_REGISTRATION)
    private TimeRegistration timeRegistration;

    @Inject
    private TimeRegistrationService timeRegistrationService;

    @Inject
    private CommentHistoryService commentHistoryService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
    }

    @Override
    protected Dialog onCreateDialog(int dialogId) {
        Dialog dialog = null;
        switch(dialogId) {
            case Constants.Dialog.TIME_REGISTRATION_ACTION: {
                boolean enterNewComment = false;
                if (StringUtils.isBlank(timeRegistration.getComment())) {
                    enterNewComment = true;
                }
                Log.d(LOG_TAG, "Entering a new comment? " + enterNewComment);

                Log.d(LOG_TAG, "Creating enter comment dialog for a time registration");
                AlertDialog.Builder commentDialog = new AlertDialog.Builder(this);

                final Context mContext = AddEditTimeRegistrationsComment.this;
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.dialog_time_registration_actions,
                                               (ViewGroup) findViewById(R.id.dialog_layout_root));
                layout.findViewById(R.id.tr_action_spinner).setVisibility(View.GONE);
                if (enterNewComment) {
                    commentDialog.setTitle(R.string.lbl_registration_add_comment);
                } else {
                    commentDialog.setTitle(R.string.lbl_registration_edit_comment);
                }

                commentDialog.setCancelable(false);
                commentDialog.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(LOG_TAG, "CommentHistory entered, ready to save...");
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
                        EditText commentEditText = (EditText) layout.findViewById(R.id.tr_comment);
                        String comment = commentEditText.getText().toString();
                        ContextUtils.hideKeyboard(mContext, commentEditText);
                        Log.d(LOG_TAG, "Time Registration will be saved with comment: " + comment);
                        updateComment(comment);
                    }
                });
                commentDialog.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(LOG_TAG, "Ending time registration cancelled on entering comment...");
                        removeDialog(Constants.Dialog.TIME_REGISTRATION_ACTION);
                        finish();
                    }
                });

                final EditText commentEditText = (EditText) layout.findViewById(R.id.tr_comment);
                Button reuseComment = (Button) layout.findViewById(R.id.tr_reuse_btn);
                reuseComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String comment = commentHistoryService.findLastComment();
                        if (comment != null) {
                            commentEditText.setText(comment);
                        }
                    }
                });

                if (!enterNewComment) {
                    commentEditText.setText(timeRegistration.getComment());
                }

                commentDialog.setView(layout);
                dialog = commentDialog.create();

                break;
            }
        };
        return dialog;
    }

    /**
     * Updates the time registration with a new comment. If the comment is empty or null the comment will be erased.
     * @param comment The comment to update on the {@link TimeRegistration} instance.
     */
    private void updateComment(String comment) {
        if (StringUtils.isNotBlank(comment)) {
            timeRegistration.setComment(comment);
            commentHistoryService.updateLastComment(comment);
        } else {
            timeRegistration.setComment(null);
        }
        timeRegistrationService.update(timeRegistration);

        setResult(RESULT_OK);
        finish();
    }
}