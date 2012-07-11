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

package eu.vranckaert.worktime.service.ui;

import eu.vranckaert.worktime.model.Project;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 09/02/11
 * Time: 19:13
 */
public interface WidgetService {
    /**
     * Updates all widgets to match the current application state.
     */
    void updateAllWidgets();

    /**
     * Updates all widgets for which the id is specified in the list.
     * @param widgetIds The list of id's defining which widgets should be updated.
     */
    void updateWidgets(List<Integer> widgetIds);

    /**
     * Updates all the widgets that are configured for the specified {@link Project}.
     * @param project Based on this variable a lookup is done on
     * {@link eu.vranckaert.worktime.model.WidgetConfiguration} to check which widgets are configured for this
     * {@link Project}. All linked widgets will be updated.
     */
    void updateWidgetsForProject(Project project);

    /**
     * Update the widget with a certain id. This will forward the call to the method that will handle the request for
     * widgets of this size.
     * @param id The id of the widget to be updated.
     */
    void updateWidget(int id);

    /**
     * Updates the widget's content for the 1x1 widgets.
     * @param id The id of the widget to be updated.
     */
    void updateWidget1x1(int id);

    /**
     * Updates the widget's content for the 2x2 widgets.
     * @param id The id of the widget to be updated.
     */
    void updateWidget2x2(int id);
}
