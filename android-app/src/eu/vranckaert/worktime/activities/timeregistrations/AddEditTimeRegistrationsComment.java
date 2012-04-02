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

        showDialog(Constants.Dialog.ENTER_COMMENT_FOR_TR);
    }

    @Override
    protected Dialog onCreateDialog(int dialogId) {
        Dialog dialog = null;
        switch(dialogId) {
            case Constants.Dialog.ENTER_COMMENT_FOR_TR: {
                boolean enterNewComment = false;
                if (StringUtils.isBlank(timeRegistration.getComment())) {
                    enterNewComment = true;
                }
                Log.d(LOG_TAG, "Entering a new comment? " + enterNewComment);

                Log.d(LOG_TAG, "Creating enter comment dialog for a time registration");
                AlertDialog.Builder enterComment = new AlertDialog.Builder(this);

                final Context mContext = AddEditTimeRegistrationsComment.this;
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.dialog_add_tr_comment,
                                               (ViewGroup) findViewById(R.id.dialog_layout_root));
                if (enterNewComment) {
                    enterComment.setTitle(R.string.lbl_widget_dialog_title_enter_comment);
                } else {
                    enterComment.setTitle(R.string.lbl_widget_dialog_title_edit_comment);
                }

                enterComment.setCancelable(false);
                enterComment.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(LOG_TAG, "CommentHistory entered, ready to save...");
                        removeDialog(Constants.Dialog.ENTER_COMMENT_FOR_TR);
//                        AutoCompleteTextView commentEditText =
//                                (AutoCompleteTextView) layout.findViewById(R.id.tr_comment);
                        EditText commentEditText = (EditText) layout.findViewById(R.id.tr_comment);
                        String comment = commentEditText.getText().toString();
                        ContextUtils.hideKeyboard(mContext, commentEditText);
                        Log.d(LOG_TAG, "Time Registration will be saved with comment: " + comment);
                        updateComment(comment);
                    }
                });
                enterComment.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(LOG_TAG, "Ending time registration cancelled on entering comment...");
                        removeDialog(Constants.Dialog.ENTER_COMMENT_FOR_TR);
                        finish();
                    }
                });

//                AutoCompleteTextView commentEditText = (AutoCompleteTextView) layout.findViewById(R.id.tr_comment);
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
//                List<String> options = commentHistoryService.getAll();
//                ArrayAdapter<String> autoCompleteAdapter =
//                        new ArrayAdapter<String>(this, R.layout.autocomplete_list_item, options);
//                commentEditText.setAdapter(autoCompleteAdapter);

                if (!enterNewComment) {
                    commentEditText.setText(timeRegistration.getComment());
                }

                enterComment.setView(layout);
                dialog = enterComment.create();

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