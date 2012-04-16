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
package eu.vranckaert.worktime.service;

import android.content.Context;
import eu.vranckaert.worktime.enums.export.CsvSeparator;
import eu.vranckaert.worktime.exceptions.export.GeneralExportException;
import jxl.format.Colour;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Dirk Vranckaert
 *         Date: 5/11/11
 *         Time: 14:44
 */
public interface ExportService {
    /**
     * The extension to be used for CSV exports
     */
    public final String CSV_EXTENSTION = "csv";
    /**
     * The extension to be used for XLS exports
     */
    public final String XLS_EXTENSTION = "xls";
    /**
     * The color of the headers in an Excel export file
     */
    public final Colour EXCEL_HEADER_COLOR = Colour.RED;

    /**
     * Write some data to a CSV file. The exported data will be stored locally.
     *
     * @param ctx       The context.
     * @param filename  The name of the file <b>WITHOUT</b> the extension. Depending on the implementation the extension
     *                  will be automatically set. If you however specify an extension it will not be overridden but the correct
     *                  extension will just be added to the filename.
     * @param headers   A list of strings with the values to be shown in the headers.
     * @param values    A list with string-arrays containing all the values to be printed. No check is executed if the
     *                  number of values horizontally equals the number of headers you specified. This may be different!
     * @param separator The {@link CsvSeparator} to be used in the file. Comma is used for MAC/UNIX systems
     * @return The exported file.
     * @throws GeneralExportException This exception means that something went wrong during export but we don't know
     *                                exactly what. Most likely it's due to a file-system issue (SD-card not mounted or not writable).
     */
    File exportCsvFile(Context ctx, String filename, List<String> headers, List<String[]> values, CsvSeparator separator) throws GeneralExportException;

    /**
     * Write some data to an Excel file. The exported data will be stored locally.
     *
     * @param ctx        The context.
     * @param filename   The name of the file <b>WITHOUT</b> the extension. Depending on the implementation the extension
     *                   will be automatically set. If you however specify an extension it will not be overridden but the correct
     *                   extension will just be added to the filename.
     * @param headers    A list of strings with the values to be shown in the headers.
     * @param values     A map containing a list with string-arrays containing all the values to be printed. No check is
     *                   executed if the number of values horizontally equals the number of headers you specified. This may be different! <br/>
     *                   The keys of the map represent the names of the sheets in the Excel workbook. Each key (aka 'sheet') will have a
     *                   list containing {@link Object} arrays. Every position in the list represents a row, every position in the array
     *                   represents a cell-value. <br/>
     *                   If one of the cell values starts with an equal sign (<b>=</b>) then the mathematical operation will be resolved
     *                   automatically. If you use column references like <b>=A+B</b> then we will translate this row by row to
     *                   <b>=A1+B1</b>, <b>=A2+B2</b>, ... If you do not want to resolve the mathematical operation you will have to start
     *                   the cell value with a single quote: <br>'=</br>. The cell values will be displayed starting from row 2 if the
     *                   headers are
     *                   available. If no headers are available the values will start at row 0.
     * @return The exported file.
     * @throws GeneralExportException This exception means that something went wrong during export but we don't know
     *                                exactly what. Most likely it's due to a file-system issue (SD-card not mounted or not writable).
     */
    File exportXlsFile(Context ctx, String filename, Map<String, List<String>> headers, Map<String, List<Object[]>> values) throws GeneralExportException;

}
