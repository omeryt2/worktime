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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.inject.Inject;
import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.constants.Constants;
import eu.vranckaert.worktime.enums.export.ExportCsvSeparator;
import eu.vranckaert.worktime.enums.export.ExportData;
import eu.vranckaert.worktime.enums.export.ExportType;
import eu.vranckaert.worktime.exceptions.export.GeneralExportException;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.dto.export.ExportDTO;
import eu.vranckaert.worktime.model.dto.reporting.ReportingTableRecord;
import eu.vranckaert.worktime.model.dto.reporting.datalevels.ReportingDataLvl0;
import eu.vranckaert.worktime.model.dto.reporting.datalevels.ReportingDataLvl1;
import eu.vranckaert.worktime.model.dto.reporting.datalevels.ReportingDataLvl2;
import eu.vranckaert.worktime.service.ExportService;
import eu.vranckaert.worktime.utils.context.ContextUtils;
import eu.vranckaert.worktime.utils.context.IntentUtil;
import eu.vranckaert.worktime.utils.date.DateFormat;
import eu.vranckaert.worktime.utils.date.DateUtils;
import eu.vranckaert.worktime.utils.date.TimeFormat;
import eu.vranckaert.worktime.utils.preferences.Preferences;
import eu.vranckaert.worktime.utils.string.StringUtils;
import jxl.biff.DisplayFormat;
import org.joda.time.Period;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: DIRK VRANCKAERT
 * Date: 15/02/11
 * Time: 00:15
 */
public class ReportingExportActivity extends GuiceActivity {
    private static final String LOG_TAG = ReportingExportActivity.class.getSimpleName();

    @InjectView(R.id.reporting_export_type)
    private Spinner reportingTypeSpinner;
    @InjectView(R.id.reporting_export_filename)
    private EditText fileNameInput;
    @InjectView(R.id.reporting_export_filename_required)
    private TextView fileNameInputRequired;
    @InjectView(R.id.reporting_export_csv_separator_container)
    private View reportingCsvSeparatorContainer;
    @InjectView(R.id.reporting_export_csv_separator)
    private Spinner reportingCsvSeparatorSpinner;
    @InjectView(R.id.reporting_export_data_container)
    private View reportingDataContainer;
    @InjectView(R.id.reporting_export_data)
    private Spinner reportingDataSpinner;

    @Inject
    private SharedPreferences preferences;

    @Inject
    private ExportService exportService;

    @InjectExtra(value = Constants.Extras.EXPORT_DTO)
    private ExportDTO exportDto;

    private File exportedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reporting_export);

        initForm(ReportingExportActivity.this);
    }

    /**
     * Updates the entire form when launching this activity.
     *
     * @param ctx The context of the activity.
     */
    private void initForm(Context ctx) {
        // Type
        ArrayAdapter reportingTypeAdapter = ArrayAdapter.createFromResource(
                this, R.array.array_reporting_export_type_options, android.R.layout.simple_spinner_item);
        reportingTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportingTypeSpinner.setAdapter(reportingTypeAdapter);
        reportingTypeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ExportType exportType = ExportType.getByIndex(position);
                updateViewsForExportType(exportType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // NA
            }
        });

        // Filename
        fileNameInput.setText(Preferences.getReportingExportFileName(ctx));

        // CSV Separator
        ArrayAdapter reportingCsvSeparatorAdapter = ArrayAdapter.createFromResource(
                this, R.array.array_reporting_export_csv_separator_options, android.R.layout.simple_spinner_item);
        reportingCsvSeparatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportingCsvSeparatorSpinner.setAdapter(reportingCsvSeparatorAdapter);

        // Data
        ArrayAdapter reportingDataAdapter = ArrayAdapter.createFromResource(
                this, R.array.array_reporting_export_data_options, android.R.layout.simple_spinner_item);
        reportingDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportingDataSpinner.setAdapter(reportingDataAdapter);

        // Set initial data
        updateExportType(ctx);
        updateExportCsvSeparator(ctx);
        updateExportData(ctx);
        updateViewsForExportType(ExportType.getByIndex(reportingTypeSpinner.getSelectedItemPosition()));
    }

    /**
     * Updates the entire view for the selected export type.
     * @param exportType The selected {@link ExportType}.
     */
    private void updateViewsForExportType(ExportType exportType) {
        switch (exportType) {
            case CSV: {
                reportingCsvSeparatorContainer.setVisibility(View.VISIBLE);
                reportingDataContainer.setVisibility(View.VISIBLE);
                break;
            }
            case XLS: {
                reportingCsvSeparatorContainer.setVisibility(View.GONE);
                reportingDataContainer.setVisibility(View.GONE);
                break;
            }
        }
    }

    /**
     * Updates the view elements specified for showing the export type with the default settings.
     * @param ctx The context.
     */
    private void updateExportType(Context ctx) {
        ExportType exportType = Preferences.getPreferredExportType(ctx);
        reportingTypeSpinner.setSelection(exportType.getPosition());
    }

    /**
     * Updates the view elements specified for showing the export csv separator with the default settings.
     * @param ctx The context.
     */
    private void updateExportCsvSeparator(Context ctx) {
        ExportCsvSeparator exportCsvSeparator = Preferences.getPreferredExportCSVSeparator(ctx);
        reportingCsvSeparatorSpinner.setSelection(exportCsvSeparator.getPosition());
    }

    /**
     * Updates the view elements specified for showing the export data with the default settings.
     * @param ctx The context.
     */
    private void updateExportData(Context ctx) {
        ExportData exportData = Preferences.getPreferredExportData(ctx);
        reportingDataSpinner.setSelection(exportData.getPosition());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d(LOG_TAG, "Received request to create loading dialog with id " + id);
        Dialog dialog = null;
        switch (id) {
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
     *
     * @param view The view.
     */
    public void onHomeClick(View view) {
        IntentUtil.goHome(this);
    }

    /**
     * Save the file name to the preferences in case it has changed.
     * Afterwards Disk the time registrations.
     *
     * @param view The view.
     */
    public void onExportClick(View view) {
        Log.d(LOG_TAG, "Export button clicked!");
        Log.d(LOG_TAG, "Validate input...");
        if (!validate()) {
            return;
        }

        Log.d(LOG_TAG, "Update the preferences...");
        updatePreferences();

        Log.d(LOG_TAG, "Hide the soft keyboard if visible");
        ContextUtils.hideKeyboard(ReportingExportActivity.this, fileNameInput);

        if (ContextUtils.isSdCardAvailable() && ContextUtils.isSdCardWritable()) {
            startExport();
        } else {
            showDialog(Constants.Dialog.REPORTING_EXPORT_UNAVAILABLE);
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (fileNameInput.getText().toString().length() < 3) {
            Log.d(LOG_TAG, "Validation failed! Showing applicable error messages...");
            fileNameInputRequired.setVisibility(View.VISIBLE);
            valid = false;
        }

        if (valid) {
            Log.d(LOG_TAG, "Validation successful. Hiding all error messages...");
            fileNameInputRequired.setVisibility(View.GONE);
        }
        return valid;
    }

    private void updatePreferences() {
        ExportType exportType = ExportType.getByIndex(reportingTypeSpinner.getSelectedItemPosition());
        String filename = fileNameInput.getText().toString();
        Log.d(LOG_TAG, "Save the (changed) filename and export type in the preferences");
        Preferences.setReportingExportFileName(ReportingExportActivity.this, fileNameInput.getText().toString());
        Preferences.setPreferredExportType(ReportingExportActivity.this, exportType);

        if (reportingCsvSeparatorSpinner.getVisibility() == View.VISIBLE) {
            Log.d(LOG_TAG, "Save the (changed) CSV separator in the preferences");
            ExportCsvSeparator separatorExport = ExportCsvSeparator.getByIndex(reportingCsvSeparatorSpinner.getSelectedItemPosition());
            Preferences.setPreferredExportCSVSeparator(ReportingExportActivity.this, separatorExport);
        }

        if (reportingCsvSeparatorSpinner.getVisibility() == View.VISIBLE) {
            Log.d(LOG_TAG, "Save the (changed) export data in the preferences");
            ExportData exportData = ExportData.getByIndex(reportingDataSpinner.getSelectedItemPosition());
            Preferences.setPreferredExportData(ReportingExportActivity.this, exportData);
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
                ExportType exportType = ExportType.getByIndex(reportingTypeSpinner.getSelectedItemPosition());
                String filename = fileNameInput.getText().toString();

                File file = null;
                try {
                    switch (exportType) {
                        case CSV: {
                            ExportCsvSeparator separatorExport = ExportCsvSeparator.getByIndex(reportingCsvSeparatorSpinner.getSelectedItemPosition());
                            ExportData exportData = ExportData.getByIndex(reportingDataSpinner.getSelectedItemPosition());
                            file = doCSVExport(filename, separatorExport, exportData);
                            break;
                        }
                        case XLS: {
                            file = doExcelExport(filename);
                            break;
                        }
                    }
                } catch (GeneralExportException e) {
                    Log.e(LOG_TAG, "A general exception occured during export!", e);
                }
                Log.d(LOG_TAG, "Export in background process finished!");
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

    private File doCSVExport(String filename, ExportCsvSeparator separatorExport, ExportData exportData) throws GeneralExportException {
        List<String> headers = new ArrayList<String>();
        List<String[]> values = new ArrayList<String[]>();

        switch (exportData) {
            case RAW_DATA: {
                //Construct headers
                headers.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_startdate));
                headers.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_starttime));
                headers.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_enddate));
                headers.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_endtime));
                headers.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_comment));
                headers.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_project));
                headers.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_task));
                headers.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_projectcomment));
                //Construct body
                for (TimeRegistration timeRegistration : exportDto.getTimeRegistrations()) {
                    String startDate = DateUtils.DateTimeConverter.convertDateToString(timeRegistration.getStartTime(), DateFormat.SHORT, ReportingExportActivity.this);
                    String startTime = DateUtils.DateTimeConverter.convertTimeToString(timeRegistration.getStartTime(), TimeFormat.MEDIUM, ReportingExportActivity.this);
                    String endDate = "";
                    String endTime = "";
                    String trComment = "";
                    String projectName = timeRegistration.getTask().getProject().getName();
                    String taskName = timeRegistration.getTask().getName();
                    String projectComment = "";

                    if(timeRegistration.getEndTime() != null) {
                        endDate = DateUtils.DateTimeConverter.convertDateToString(timeRegistration.getEndTime(), DateFormat.SHORT, ReportingExportActivity.this);
                        endTime = DateUtils.DateTimeConverter.convertTimeToString(timeRegistration.getEndTime(), TimeFormat.MEDIUM, ReportingExportActivity.this);
                    } else {
                        endDate = getString(R.string.now);
                        endTime = "";
                    }
                    if (StringUtils.isNotBlank(timeRegistration.getComment())) {
                        trComment = timeRegistration.getComment();
                    }
                    if (StringUtils.isNotBlank(timeRegistration.getTask().getProject().getComment())) {
                        projectComment = timeRegistration.getTask().getProject().getComment();
                    }

                    String[] exportLine = {
                            startDate, startTime, endDate, endTime, trComment,
                            projectName, taskName, projectComment
                    };
                    values.add(exportLine);
                }
                break;
            }
            case REPORT: {
                //Construct headers
                headers = null;
                //Construct body
                for (ReportingTableRecord tableRecord : exportDto.getTableRecords()) {
                    String[] exportLine = {
                            tableRecord.getColumn1(),
                            tableRecord.getColumn2(),
                            tableRecord.getColumn3(),
                            tableRecord.getColumnTotal()
                    };
                    values.add(exportLine);
                }
                break;
            }
        }

        return exportService.exportCsvFile(ReportingExportActivity.this, filename, headers, values, separatorExport);
    }

    private File doExcelExport(String filename) throws GeneralExportException {
        String reportSheetName = "Report";
        String dataSheetName = "Data";

        List<Object> reportHeaders = new ArrayList<Object>();
        List<Object[]> reportValues = new ArrayList<Object[]>();

        List<Object> rawHeaders = new ArrayList<Object>();
        List<Object[]> rawValues = new ArrayList<Object[]>();

        //Construct report body
        reportValues = buildReportBody(exportDto.getTimeRegistrations(), exportDto.getReportingDataLevels());
        //Construct report headers
        if (reportValues.size() > 0) {
            reportHeaders = Arrays.asList(reportValues.get(0));
            reportValues.remove(0);
        }

        //Construct raw headers
        rawHeaders.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_startdate));
        rawHeaders.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_starttime));
        rawHeaders.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_enddate));
        rawHeaders.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_endtime));
        rawHeaders.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_comment));
        rawHeaders.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_project));
        rawHeaders.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_task));
        rawHeaders.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_projectcomment));
        rawHeaders.add("");
        rawHeaders.add("");
        rawHeaders.add(getString(R.string.lbl_reporting_results_export_raw_data_csv_total_time));
        //Construct raw body
        rawValues = buildRawBody(exportDto.getTimeRegistrations());

        Map<String, List<Object>> headers = new HashMap<String, List<Object>>();
        Map<String, Map<Integer, DisplayFormat>> headersColumnFormat = new HashMap<String, Map<Integer, DisplayFormat>>();
        Map<String, Map<Integer, DisplayFormat>> valuesColumnFormat = new HashMap<String, Map<Integer, DisplayFormat>>();
        Map<String, List<Integer>> hiddenColumns = new HashMap<String, List<Integer>>();
        Map<String, List<Object[]>> values = new HashMap<String, List<Object[]>>();

        // Add all vars for the report sheet
        // Headers
        headers.put(reportSheetName, reportHeaders);
        // Values
        values.put(reportSheetName, reportValues);
        // Headers column formats
        Map<Integer, DisplayFormat> reportColumnFormat = new HashMap<Integer, DisplayFormat>();
        reportColumnFormat.put(3, new jxl.write.DateFormat("[h]:mm"));
        valuesColumnFormat.put(reportSheetName, reportColumnFormat);
        headersColumnFormat.put(reportSheetName, reportColumnFormat);

        // Add all vars for the data sheet
        // Headers
        headers.put(dataSheetName, rawHeaders);
        // Values
        values.put(dataSheetName, rawValues);
        // Value column formats
        Map<Integer, DisplayFormat> dataValuesColumnFormat = new HashMap<Integer, DisplayFormat>();
        dataValuesColumnFormat.put(0, new jxl.write.DateFormat("dd/mm/yyyy"));
        dataValuesColumnFormat.put(1, new jxl.write.DateFormat("hh:mm"));
        dataValuesColumnFormat.put(2, new jxl.write.DateFormat("dd/mm/yyyy"));
        dataValuesColumnFormat.put(3, new jxl.write.DateFormat("hh:mm"));
        dataValuesColumnFormat.put(8, new jxl.write.DateFormat("dd/mm/yyyy hh:mm"));
        dataValuesColumnFormat.put(9, new jxl.write.DateFormat("dd/mm/yyyy hh:mm"));
        dataValuesColumnFormat.put(10, new jxl.write.DateFormat("[h]:mm"));
        valuesColumnFormat.put(dataSheetName, dataValuesColumnFormat);
        // Hidden columns
        hiddenColumns.put(dataSheetName, Arrays.asList(new Integer[]{8, 9}));

        try {
            return exportService.exportXlsFile(ReportingExportActivity.this, filename, headers, values, headersColumnFormat, valuesColumnFormat, hiddenColumns, null, false);
        } catch (GeneralExportException e) {
            throw e;
        }
    }

    private List<Object[]> buildReportBody(List<TimeRegistration> timeRegistrations, List<ReportingDataLvl0> reportingDataLevels) {
        boolean containsOngoingTr = false;
        Date ongoingTrEndDate = null;

        int numberOfColumns = 4;
        int startRow = 0;

        List<Object[]> tableRecords = new ArrayList<Object[]>();

        Object[] headerRecord = new Object[numberOfColumns];
        headerRecord[0] = getText(R.string.lbl_reporting_results_export_report_data_total_time).toString();
        headerRecord[1] = "";
        headerRecord[2] = "";
        tableRecords.add(headerRecord);
        List<Integer> totalRowsForHeader = new ArrayList<Integer>();
        for (ReportingDataLvl0 lvl0 : reportingDataLevels) {
            Object[] lvl0Record = new Object[numberOfColumns];
            lvl0Record[0] = String.valueOf(lvl0.getKey());
            lvl0Record[1] = "";
            lvl0Record[2] = "";
            tableRecords.add(lvl0Record);
            totalRowsForHeader.add(startRow + tableRecords.size());
            List<Integer> totalRowsForLvl0 = new ArrayList<Integer>();
            for (ReportingDataLvl1 lvl1 : lvl0.getReportingDataLvl1()) {
                Object[] lvl1Record = new Object[numberOfColumns];
                lvl1Record[0] = "";
                lvl1Record[1] = String.valueOf(lvl1.getKey());
                lvl1Record[2] = "";
                tableRecords.add(lvl1Record);
                totalRowsForLvl0.add(startRow + tableRecords.size());
                int startRowLvl2 = -1;
                int endRowLvl2 = -1;
                for (ReportingDataLvl2 lvl2 : lvl1.getReportingDataLvl2()) {
                    Object[] lvl2Record = new Object[numberOfColumns];
                    lvl2Record[0] = "";
                    lvl2Record[1] = "";
                    lvl2Record[2] = String.valueOf(lvl2.getKey());
                    lvl2Record[3] = getExcelTimeFromPeriod(DateUtils.TimeCalculator.calculatePeriod(ReportingExportActivity.this, lvl2.getTimeRegistrations()));
                    tableRecords.add(lvl2Record);

                    for (TimeRegistration tr : lvl2.getTimeRegistrations()) {
                        if (tr.isOngoingTimeRegistration()) {
                            containsOngoingTr = true;
                            ongoingTrEndDate = new Date();
                            break;
                        }
                    }

                    if (startRowLvl2 < 0) {
                        startRowLvl2 = startRow + tableRecords.size();
                    }
                    endRowLvl2 = startRow + tableRecords.size();
                    Log.d(LOG_TAG, "Start row lvl2 (" + lvl2Record[2] + "): " + startRowLvl2 + " and end row lvl2: " + endRowLvl2);
                }
                lvl1Record[3] = "=SUM([CC]" + startRowLvl2 + ":[CC]" + endRowLvl2 + ")";
                Log.d(LOG_TAG, "Formula for lvl 1 (" + lvl1Record[1] + "): " + lvl1Record[3]);
            }
            String formulaLvl0 = "=";
            for (Integer totalRow : totalRowsForLvl0) {
                formulaLvl0 += "[CC]" + totalRow + "+";
            }
            formulaLvl0 = formulaLvl0.substring(0, formulaLvl0.length()-1);
            lvl0Record[3] = formulaLvl0;
        }

        String formulaHeader = "=";
        for (Integer totalRow : totalRowsForHeader) {
            formulaHeader += "[CC]" + totalRow + "+";
        }
        formulaHeader = formulaHeader.substring(0, formulaHeader.length()-1);
        headerRecord[3] = formulaHeader;

        if (containsOngoingTr) {
            // Add an empty row
            Object[] emptyRecord = new Object[numberOfColumns];
            tableRecords.add(emptyRecord);
            // Add the warning row
            Object[] warningRecord = new Object[numberOfColumns];
            String reportGenerationDate = DateUtils.DateTimeConverter.convertDateTimeToString(ongoingTrEndDate, DateFormat.SHORT, TimeFormat.MEDIUM, ReportingExportActivity.this);
            warningRecord[0] = getString(R.string.lbl_reporting_results_export_report_data_warning_ongoing_registration, reportGenerationDate);
            tableRecords.add(warningRecord);
        }

        return tableRecords;
    }

    private List<Object[]> buildRawBody(List<TimeRegistration> timeRegistrations) {
        List<Object[]> rawValues = new ArrayList<Object[]>();
        for (TimeRegistration timeRegistration : timeRegistrations) {
            Date startDate = timeRegistration.getStartTime();
            Date startTime = timeRegistration.getStartTime();
            Date endDate = null;
            Date endTime = null;
            String trComment = "";
            String projectName = timeRegistration.getTask().getProject().getName();
            String taskName = timeRegistration.getTask().getName();
            String projectComment = "";
            Date startDateTime = timeRegistration.getStartTime();
            Date endDateTime = null;

            if(timeRegistration.getEndTime() != null) {
                endDate = timeRegistration.getEndTime();
                endTime = timeRegistration.getEndTime();
                endDateTime = timeRegistration.getEndTime();
            }
            if (StringUtils.isNotBlank(timeRegistration.getComment())) {
                trComment = timeRegistration.getComment();
            }
            if (StringUtils.isNotBlank(timeRegistration.getTask().getProject().getComment())) {
                projectComment = timeRegistration.getTask().getProject().getComment();
            }

            String totalDuration = "=IF(J[CR]=\"\",NOW()-I[CR],J[CR]-I[CR])";

            Object[] exportLine = {
                    startDate, startTime, endDate, endTime, trComment,
                    projectName, taskName, projectComment, startDateTime, endDateTime, totalDuration
            };
            rawValues.add(exportLine);
        }
        return rawValues;
    }

    private static Date getExcelTimeFromPeriod(Period period) {
        Log.d(LOG_TAG, "Creating excel calendar date-time for period " + period);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1899);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //cal.add(Calendar.HOUR, -36);

        Log.d(LOG_TAG, "Default excel calendar before adding time: " + cal.getTime());

        int hours = period.getHours();
        int minutes = period.getMinutes();
        int seconds = period.getSeconds();

        if (hours > 0) {
            Log.d(LOG_TAG, "About to add hours to the excel calendar...");
            cal.add(Calendar.HOUR, hours);
            Log.d(LOG_TAG, "Default excel calendar after adding hours: " + cal.getTime());
        }
        if (minutes > 0) {
            Log.d(LOG_TAG, "About to add minutes to the excel calendar...");
            cal.set(Calendar.MINUTE, minutes);
            Log.d(LOG_TAG, "Default excel calendar after adding minutes: " + cal.getTime());
        }
        if (seconds > 0) {
            Log.d(LOG_TAG, "About to add seconds to the excel calendar...");
            cal.set(Calendar.SECOND, seconds);
            Log.d(LOG_TAG, "Default excel calendar after adding seconds: " + cal.getTime());
        }

        return cal.getTime();
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
