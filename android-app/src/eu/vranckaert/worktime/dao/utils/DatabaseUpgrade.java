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

package eu.vranckaert.worktime.dao.utils;

/**
 * User: DIRK VRANCKAERT
 * Date: 05/08/11
 * Time: 19:14
 */
public enum DatabaseUpgrade {
    UPGRADE1(21, new String[]{
            "alter table project add column flags " + DataTypes.TEXT + ";",
            "alter table task add column flags " + DataTypes.TEXT + ";",
            "alter table commenthistory add column flags " + DataTypes.TEXT + ";",
            "alter table timeregistration add column flags " + DataTypes.TEXT + ";",
            "alter table project add column finished " + DataTypes.BOOLEAN + " default 0;",
            "alter table task add column finished " + DataTypes.BOOLEAN + " default 0;",
    }),
    UPGRADE2(23, new String[] {
            "CREATE TABLE WidgetConfiguration " +
            "(" +
                "id " + DataTypes.INTEGER + " PRIMARY KEY, " +
                "projectId " + DataTypes.INTEGER +
            ");"
    }),
    UPGRADE3(24, new String[] {
            "ALTER TABLE WidgetConfiguration add column taskId " + DataTypes.INTEGER + ";"
    })
    ;

    int toVersion;
    String[] sqlQueries;

    DatabaseUpgrade(int toVersion, String[] sqlQueries) {
        this.toVersion = toVersion;
        this.sqlQueries = sqlQueries;
    }

    public int getToVersion() {
        return toVersion;
    }

    public void setToVersion(int toVersion) {
        this.toVersion = toVersion;
    }

    public String[] getSqlQueries() {
        return sqlQueries;
    }

    public void setSqlQueries(String[] sqlQueries) {
        this.sqlQueries = sqlQueries;
    }

    private class DataTypes {
        private static final String SMALLINT = "SMALLINT";
        private static final String BIGINT = "BIGINT";
        private static final String INTEGER = "INTEGER";
        private static final String BOOLEAN = "SMALLINT";
        private static final String TEXT = "TEXT";
        private static final String VARCHAR = "VARCHAR";
    }
}
