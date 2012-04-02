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

package eu.vranckaert.worktime.activities.reporting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.inject.Inject;
import com.google.inject.internal.Nullable;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.enums.export.CsvSeparator;
import eu.vranckaert.worktime.exceptions.export.GeneralExportException;
import eu.vranckaert.worktime.service.ExportService;
import eu.vranckaert.worktime.utils.context.ContextUtils;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.string.StringUtils;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 15/02/11
 * Time: 00:15
 */
public class ReportingExportActivity extends GuiceActivity {
    private static final String LOG_TAG = ReportingExportActivity.class.getSimpleName();

    @InjectView(R.id.reporting_export_filename) private EditText fileNameInput;
    @InjectView(R.id.reporting_export_filename_required) private TextView fileNameInputRequired;
    @InjectView(R.id.reporting_export_csv_separator_btn) private Button csvSeparatorBtn;

    @Inject private SharedPreferences preferences;

    @Inject private ExportService exportService;

    @InjectExtra(value = Constants.Extras.EXPORT_HEADERS, optional = true)
    @Nullable
    private List<String> exportHeaders;

    @InjectExtra(Constants.Extras.EXPORT_VALUES)
    private List<String[]> exportValues;

    private File exportedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reporting_export);

        initForm(ReportingExportActivity.this);
    }

    /**
     * Updates the entire form when launching this activity.
     * @param ctx The context of the activity.
     */
    private void initForm(Context ctx) {
        fileNameInput.setText(Preferences.getReportingExportFileName(ctx));

        updateCsvSeparator(ctx);

        csvSeparatorBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog(Constants.Dialog.REPORTING_EXPORT_CHOOSE_CSV_SEPARATOR);
            }
        });
    }

    /**
     * Updates the view elements specified for showing the chosen CSV seperator if the chosen filetype is CSV!
     * @param ctx The context.
     */
    private void updateCsvSeparator(Context ctx) {
        CsvSeparator csvSeparator = Preferences.getReportingExportCsvSeparator(ctx);
        switch (csvSeparator) {
            case COMMA:
                csvSeparatorBtn.setText(R.string.lbl_reporting_export_csv_separator_comma);
                break;
            case SEMICOLON:
                csvSeparatorBtn.setText(R.string.lbl_reporting_export_csv_separator_semicolon);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d(LOG_TAG, "Received request to create loading dialog with id " + id);
        Dialog dialog = null;
        switch(id) {
            case Constants.Dialog.REPORTING_EXPORT_UNAVAILABLE: {
                AlertDialog.Builder alertExportUnavailable = new AlertDialog.Builder(this);
				alertExportUnavailable.setTitle(R.string.msg_reporting_export_sd_unavailable)
                           .setMessage(R.string.msg_reporting_export_sd_unavailable_detail)
						   .setCancelable(false)
						   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.cancel();
                               }
                           });
				dialog = alertExportUnavailable.create();
                break;
            }
            case Constants.Dialog.REPORTING_EXPORT_CHOOSE_CSV_SEPARATOR: {
                CsvSeparator selectedSeparator = Preferences.getReportingExportCsvSeparator(this);
                final CsvSeparator[] availableSeparators = CsvSeparator.values();
                int selectedItem = -1;

                List<String> fileTypes = new ArrayList<String>();
                for (CsvSeparator separator : availableSeparators) {
                    switch(separator) {
                        case COMMA:
                            fileTypes.add(getString(R.string.lbl_reporting_export_csv_separator_comma));
                            break;
                        case SEMICOLON:
                            fileTypes.add(getString(R.string.lbl_reporting_export_csv_separator_semicolon));
                            break;
                    }
                    if(separator.equals(selectedSeparator)) {
                        selectedItem = fileTypes.size()-1;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.lbl_reporting_export_csv_separator_chooser_title)
                       .setSingleChoiceItems(
                        StringUtils.convertListToArray(fileTypes),
                        selectedItem, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int index) {
                                Preferences.setReportingExportCsvSeparator(
                                        ReportingExportActivity.this,
                                        availableSeparators[index]
                                );
                                removeDialog(Constants.Dialog.REPORTING_EXPORT_CHOOSE_CSV_SEPARATOR);
                                ReportingExportActivity.this.updateCsvSeparator(
                                    ReportingExportActivity.this
                                );
                            }
                        })
                      .setOnCancelListener(new DialogInterface.OnCancelListener() {
                          public void onCancel(DialogInterface dialogInterface) {
                              ReportingExportActivity.this.updateCsvSeparator(
                                   ReportingExportActivity.this
                              );
                          }
                      });
                dialog = builder.create();
                break;
            }
            case Constants.Dialog.REPORTING_EXPORT_LOADING: {
                dialog = ProgressDialog.show(
                        ReportingExportActivity.this,
                        "",
                        getString(R.string.msg_reporting_export_saving_sd),
                        true,
                        false
                );
                break;
            }
            case Constants.Dialog.REPORTING_EXPORT_DONE: {
                AlertDialog.Builder alertExportDone = new AlertDialog.Builder(this);
				alertExportDone
						   .setMessage(getString(R.string.msg_reporting_export_done, exportedFile.getAbsolutePath()))
						   .setCancelable(true)
						   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
                                    removeDialog(Constants.Dialog.REPORTING_EXPORT_DONE);
								}
							})
						   .setNegativeButton(R.string.msg_reporting_export_share_file, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
                                    removeDialog(Constants.Dialog.REPORTING_EXPORT_DONE);
                                    sendExportedFileByMail();
								}
							});
				dialog = alertExportDone.create();
                break;
            }
	    case Constants.Dialog.REPORTING_EXPORT_ERROR: {
		AlertDialog.Builder alertExportError = new AlertDialog.Builder(this);
		alertExportError
		    .setTitle(R.string.dialog_title_error)
		    .setMessage(R.string.msg_reporting_export_error)
		    .setCancelable(true)
		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			    removeDialog(Constants.Dialog.REPORTING_EXPORT_ERROR);
			}
		    });
		dialog = alertExportError.create();
		break;
	    }
            default:
                Log.d(LOG_TAG, "Dialog id " + id + " is not supported in this activity!");
        }
        return dialog;
    }

    /**
     * Go Home.
     * @param view The view.
     */
    public void onHomeClick(View view) {
        IntentUtil.goHome(this);
    }

    /**
     * Save the file name to the preferences in case it has changed.
     * Afterwards Disk the time registrations.
     * @param view The view.
     */
    public void onExportClick(View view) {
        Log.d(LOG_TAG, "Disk button clicked!");
        Log.d(LOG_TAG, "Validate input...");
        if(fileNameInput.getText().toString().length() < 3) {
            Log.d(LOG_TAG, "Validation failed! Showing applicable error messages...");
            fileNameInputRequired.setVisibility(View.VISIBLE);
            return;
        } else {
            Log.d(LOG_TAG, "Validation successful. Hiding all error messages...");
            fileNameInputRequired.setVisibility(View.GONE);
            Log.d(LOG_TAG, "Save the (changed) filename");
            Preferences.setReportingExportFileName(ReportingExportActivity.this, fileNameInput.getText().toString());
            Log.d(LOG_TAG, "Hide the soft keyboard if visible");
            ContextUtils.hideKeyboard(ReportingExportActivity.this, fileNameInput);
        };

        if(ContextUtils.isSdCardAvailable() && ContextUtils.isSdCardWritable()) {
            startExport();
        } else {
            showDialog(Constants.Dialog.REPORTING_EXPORT_UNAVAILABLE);
        }
    }

    private void startExport() {
        AsyncTask task = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                Log.d(LOG_TAG, "About to show loading dialog for export");
                showDialog(Constants.Dialog.REPORTING_EXPORT_LOADING);
                Log.d(LOG_TAG, "Loading dialog for export showing!");
            }

            @Override
            protected Object doInBackground(Object... objects) {
                Log.d(LOG_TAG, "Starting export background process...");
                String filename = fileNameInput.getText().toString();
                CsvSeparator separator = Preferences.getReportingExportCsvSeparator(ReportingExportActivity.this);
		File file = null;
		try {
		    file = exportService.exportCsvFile(ReportingExportActivity.this, filename, exportHeaders, exportValues, separator);
		} catch (GeneralExportException e) {
		    Log.e(LOG_TAG, "A general exception occured during export!", e);
		}
		Log.d(LOG_TAG, "Disk in background process finished!");
                return file;
            }

            @Override
            protected void onPostExecute(Object o) {
                Log.d(LOG_TAG, "About to remove loading dialog for export");
                removeDialog(Constants.Dialog.REPORTING_EXPORT_LOADING);
                Log.d(LOG_TAG, "Loading dialog for export removed!");
		
		if (o == null) {
		    showDialog(Constants.Dialog.REPORTING_EXPORT_ERROR);
		    return;
		}

                exportedFile = (File) o;
                showDialog(Constants.Dialog.REPORTING_EXPORT_DONE);
            }
        }.execute();
    }

    private void sendExportedFileByMail() {
        IntentUtil.sendSomething(
                ReportingExportActivity.this,
                R.string.lbl_reporting_export_share_subject,
                R.string.lbl_reporting_export_share_body,
                exportedFile,
                R.string.lbl_reporting_export_share_file_app_chooser_title
        );
    }
}
