/*
 *  Copyright 2012 Dirk Vranckaert
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.vranckaert.worktime.dao;

import eu.vranckaert.worktime.dao.impl.ProjectDaoImpl;
import eu.vranckaert.worktime.dao.impl.TaskDaoImpl;
import eu.vranckaert.worktime.dao.impl.TimeRegistrationDaoImpl;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.test.Assert;
import eu.vranckaert.worktime.test.cases.DaoTestCase;
import eu.vranckaert.worktime.test.data.TimeRegistrationTestData;

import java.util.Date;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 26/03/12
 * Time: 14:43
 */
public class TimeRegistrationDaoTest extends DaoTestCase<TimeRegistrationDao, TimeRegistrationDaoImpl> {
    private TimeRegistrationTestData testData;

    public TimeRegistrationDaoTest() {
        super(TimeRegistrationDaoImpl.class);
    }
    
    private void setupDatabase() {
        ProjectDao projectDao = getDaoForClass(ProjectDao.class, ProjectDaoImpl.class);
        TaskDao taskDao = getDaoForClass(TaskDao.class, TaskDaoImpl.class);
        testData = TimeRegistrationTestData.getInstance(getDao(), projectDao, taskDao);
    }

    public void testGetLatestTimeRegistration() {
        setupDatabase();

        TimeRegistration tr = getDao().getLatestTimeRegistration();
        Date startTime = tr.getStartTime();
        Date endTime = tr.getEndTime();

        Date expectedStartTime = testData.getDateTime(2011, 10, 23, 12, 30, 30, 0);
        Date expectedEndTime = testData.getDateTime(2011, 10, 23, 16, 0, 30, 0);

        Assert.assertSameDate(expectedStartTime, startTime);
        Assert.assertSameDate(expectedEndTime, endTime);
    }
    
    public void testFindTimeRegistrationsForTask() {
        setupDatabase();

        List<TimeRegistration> result1 = getDao().findTimeRegistrationsForTask(testData.defaultTask);
        assertNotNull(result1);
        assertEquals(testData.trsForDefaultTask.size(), result1.size());

        List<TimeRegistration> result2 = getDao().findTimeRegistrationsForTask(testData.task2);
        assertNotNull(result1);
        assertEquals(testData.trsForTask2.size(), result2.size());

        List<TimeRegistration> result3 = getDao().findTimeRegistrationsForTask(testData.task3);
        assertNotNull(result3);
        assertEquals(testData.trsForTask3.size(), result3.size());
    }
}
