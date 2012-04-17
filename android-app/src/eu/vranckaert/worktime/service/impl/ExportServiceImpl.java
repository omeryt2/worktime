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

package eu.vranckaert.worktime.service.impl;

import android.content.Context;
import android.util.Log;
import eu.vranckaert.worktime.constants.TextConstants;
import eu.vranckaert.worktime.enums.Encoding;
import eu.vranckaert.worktime.enums.export.ExportCsvSeparator;
import eu.vranckaert.worktime.exceptions.export.GeneralExportException;
import eu.vranckaert.worktime.service.ExportService;
import eu.vranckaert.worktime.utils.file.FileUtil;
import eu.vranckaert.worktime.utils.string.StringUtils;
import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author Dirk Vranckaert
 *         Date: 5/11/11
 *         Time: 14:46
 */
public class ExportServiceImpl implements ExportService {
    private static final String LOG_TAG = ExportServiceImpl.class.getSimpleName();

    @Override
    public File exportCsvFile(Context ctx, String filename, List<String> headers, List<String[]> values, ExportCsvSeparator separatorExport) throws GeneralExportException {
        Character separatorChar = separatorExport.getSeparator();
        String emptyValue = "\"\"";

        StringBuilder result = new StringBuilder();

        if (headers != null && headers.size() > 0) {
            for (String header : headers) {
                if (StringUtils.isNotBlank(header)) {
                    result.append("\"" + header + "\"");
                } else {
                    result.append(emptyValue);
                }
                result.append(separatorChar);
            }
            result.append(TextConstants.NEW_LINE);
        }

        for (String[] valuesRecord : values) {
            for (int i = 0; i < valuesRecord.length; i++) {
                String value = valuesRecord[i];
                if (StringUtils.isNotBlank(value)) {
                    result.append("\"" + value + "\"");
                } else {
                    result.append(emptyValue);
                }
                result.append(separatorChar);
            }

            result.append(TextConstants.NEW_LINE);
        }

        File file = getExportFile(ctx, filename, CSV_EXTENSTION);

        FileOutputStream fos = null;
        try {
            Encoding encoding = Encoding.UTF_8;
            byte[] textBytes = encoding.encodeString(result.toString());
            byte[] bom = encoding.getByteOrderMarker();

            fos = new FileOutputStream(file);
            fos.write(bom);
            fos.write(textBytes);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "The file is not found", e);
            throw new GeneralExportException("The file is not found, probably a file-system issue...", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception occurred during export...", e);
            throw new GeneralExportException("Exception occurred during export", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Could not close the stream", e);
                }
            }
        }

        FileUtil.enableForMTP(ctx, file);

        return file;
    }

    private File getExportFile(Context ctx, String filename, String filenameExtension) throws GeneralExportException {
        File exportDir = FileUtil.getExportDir(ctx);
        FileUtil.enableForMTP(ctx, exportDir);

        File file = new File(
                exportDir,
                filename + "." + filenameExtension
        );
        FileUtil.applyPermissions(file, true, true, false);

        try {
            boolean fileAlreadyExists = file.createNewFile();

            if (fileAlreadyExists) {
                file.delete();
                file.createNewFile();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Probably a file-system issue...", e);
            throw new GeneralExportException("Probably a file-system issue...", e);
        }

        return file;
    }

    @Override
    public File exportXlsFile(Context ctx, String filename, Map<String, List<String>> headers, Map<String, List<Object[]>> values) throws GeneralExportException {
        File file = getExportFile(ctx, filename, XLS_EXTENSTION);

        int sheetIndex = 0;
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(file);
            Log.d(LOG_TAG, "Excel workbook created for file " + file.getAbsolutePath());
        } catch (IOException e) {
            String msg = "Something went wrong during the export";
            Log.e(LOG_TAG, msg, e);
            throw new GeneralExportException(msg + ": " + e.getMessage(), e);
        }

        for (Map.Entry<String, List<Object[]>> entry : values.entrySet()) {
            String sheetName = entry.getKey();
            List<Object[]> sheetValues = entry.getValue();

            WritableSheet sheet = workbook.createSheet(sheetName, sheetIndex);
            Log.d(LOG_TAG, "Sheet with name " + sheetName + " created for workbook at index " + sheetIndex);
            sheetIndex++;

            int firstDataRow = 1;
            if (headers == null || headers.get(sheetName) == null || headers.get(sheetName).size() == 0) {
                firstDataRow = 0;
                Log.d(LOG_TAG, "No headers information found so the headers will start at row 0");
            } else {
                Log.d(LOG_TAG, "Header information found, processing headers now...");
                List<String> headerValues = headers.get(sheetName);

                WritableCellFormat headerCellFormat = new WritableCellFormat();
                try {
                    headerCellFormat.setBackground(ExportService.EXCEL_HEADER_COLOR);
                } catch (WriteException e) {
                    Log.w(LOG_TAG, "Cannot change the background color of the header format!", e);
                }

                for (int i = 0; i < headerValues.size(); i++) {
                    Label headerCell = new Label(i, 0, headerValues.get(i), headerCellFormat);
                    Log.d(LOG_TAG, "Writing content to header cell at column " + i + ", row 0. Data is: " + headerValues.get(i));
                    try {
                        sheet.addCell(headerCell);
                    } catch (WriteException e) {
                        Log.w(LOG_TAG, "For some reason the header cell for column " + i + " cannot be added", e);
                    }
                }
                Log.d(LOG_TAG, "Header takes all place at row 0, data will start at row 1");
            }

            int row = firstDataRow;
            int maxColumnNumber = 0;

            for (Object[] sheetRowValues : sheetValues) {
                int column = 0;
                for (Object cellValue : sheetRowValues) {
                    if (row > maxColumnNumber)
                        maxColumnNumber = row;
                    if (cellValue != null) {
                        Label headerCell = new Label(column, row, cellValue.toString());
                        Log.d(LOG_TAG, "Writing data to Excel workbook at sheet " + sheetName + " in cell at column " + column + " and row " + row + " Data is: " + cellValue.toString());
                        try {
                            sheet.addCell(headerCell);
                        } catch (WriteException e) {
                            Log.w(LOG_TAG, "For some reason the header cell for column " + column + " and row " + row + " cannot be added", e);
                        }
                    } else {
                        Log.d(LOG_TAG, "No data found to be displayed in cell at column " + column + " and row " + row);
                    }
                    column++;
                }
                row++;
            }

            // Auto-size all columns in which we entered data on all the sheets we created to match the content of the
            // cells...
            CellView autoSizingCellView = new CellView();
            autoSizingCellView.setAutosize(true);
            for (int i=1; i <= maxColumnNumber; i++) {
                Log.d(LOG_TAG, "Auosizing cells in column " + i + " on sheet " + sheetName);
                sheet.setColumnView(i, autoSizingCellView);
            }
        }

        Log.d(LOG_TAG, "Writing workbook to local storage at " + file.getAbsolutePath());
        try {
            workbook.write();
            workbook.close();
        } catch (IOException e) {
            String msg = "A general IO Exception occured!";
            Log.e(LOG_TAG, msg, e);
            throw new GeneralExportException(msg, e);
        } catch (WriteException e) {
            String msg = "Could not write the Excel file to disk!";
            Log.e(LOG_TAG, msg, e);
            throw new GeneralExportException(msg, e);
        }

        return file;
    }

    final byte[] HEX_CHAR_TABLE = {
            (byte) '0', (byte) '1', (byte) '2', (byte) '3',
            (byte) '4', (byte) '5', (byte) '6', (byte) '7',
            (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
            (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
    };

    public String getHexString(byte[] raw) throws UnsupportedEncodingException {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, "ASCII");
    }
}
