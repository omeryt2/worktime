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
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.test.Assert;
import eu.vranckaert.worktime.test.cases.DaoTestCase;
import eu.vranckaert.worktime.test.data.TimeRegistrationTestData;

import java.util.ArrayList;
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

    public void testFindTimeRegistrationsForTasks() {
	setupDatabase();
	
	List<Task> tasks = new ArrayList<Task>();
	List<TimeRegistration> timeRegistrations = getDao().findTimeRegistrationsForTaks(tasks);
	assertNotNull(timeRegistrations);
	assertEquals("No time registrations should be found", 0, timeRegistrations.size());

	tasks.add(testData.task2);
	tasks.add(testData.task1);
	timeRegistrations = getDao().findTimeRegistrationsForTaks(tasks);

	assertNotNull(timeRegistrations);
	assertEquals(testData.trsForTask2.size() + testData.trsForTask1.size(), timeRegistrations.size());
    }
    
    public void testGetTimeRegistrationsNoTasks() {
	setupDatabase();

	List<TimeRegistration> timeRegistrations = getDao().getTimeRegistrations(
	    testData.getDateTime(2011, 10, 22, 9, 54, 0, 0),
	    testData.getDateTime(2011, 10, 23, 16, 0, 30, 0),
	    null
	);

	assertNotNull(timeRegistrations);
	assertEquals(testData.trsForDefaultTask.size() + testData.trsForTask1.size() + testData.trsForTask2.size() + testData.trsForTask3.size(), timeRegistrations.size());
    }
    
    public void testGetTimeRegistrationsWithTasks() {
	setupDatabase();

	List<Task> tasks = new ArrayList<Task>();
	tasks.add(testData.defaultTask);
	tasks.add(testData.task2);
	List<TimeRegistration> timeRegistrations = getDao().getTimeRegistrations(
	    testData.getDateTime(2011, 10, 22, 9, 54, 0, 0),
	    testData.getDateTime(2011, 10, 23, 16, 0, 30, 0),
	    tasks
	);

	assertNotNull(timeRegistrations);
	assertEquals(testData.trsForDefaultTask.size() + testData.trsForTask2.size(), timeRegistrations.size());
    }
    
    public void testGetTimeRegistrationsWithSpecificDateRange() {
	setupDatabase();

	// Exactly two time registrations are queried. The one from task 1 and the first one of task 2
	List<TimeRegistration> timeRegistrations = getDao().getTimeRegistrations(
	    testData.getDateTime(2011, 10, 22, 10, 35, 11, 0),
	    testData.getDateTime(2011, 10, 22, 23, 0, 0, 0),
	    null
	);

	assertNotNull(timeRegistrations);
	assertEquals("Exactly four time registrations should be found!", 4, timeRegistrations.size());
	for (TimeRegistration timeRegistration : timeRegistrations) {
	    assertTrue(
		timeRegistration.getTask().getId().equals(testData.defaultTask.getId())
		|| timeRegistration.getTask().getId().equals(testData.task1.getId())
		|| timeRegistration.getTask().getId().equals(testData.task2.getId())
	    );
	}
    }

    public void testFindAll() {
	setupDatabase();

	int lowerLimit = 0;
	int maxRows = 3;
	List<TimeRegistration> timeRegistrations = getDao().findAll(lowerLimit, maxRows);
	assertNotNull(timeRegistrations);
	assertEquals("Exactly " + maxRows + " time registrations should be found!", maxRows, timeRegistrations.size());
	for (TimeRegistration timeRegistration : timeRegistrations) {
	    assertTrue(
		timeRegistration.getTask().getId().equals(testData.task2.getId())
	    );
	}

	lowerLimit = 6;
	maxRows = 10;
	timeRegistrations = getDao().findAll(lowerLimit, maxRows);
	assertNotNull(timeRegistrations);
	assertEquals("Exactly zero time registrations should be found!", 0, timeRegistrations.size());

	lowerLimit = 5;
	maxRows = 10;
	timeRegistrations = getDao().findAll(lowerLimit, maxRows);
	assertNotNull(timeRegistrations);
	assertEquals("Exactly one time registrations should be found!", 1, timeRegistrations.size());
	for (TimeRegistration timeRegistration : timeRegistrations) {
	    assertTrue(timeRegistration.getTask().getId().equals(testData.defaultTask.getId()));
	}
    }

    public void testGetPreviousTimeRegistration() {
	setupDatabase();

	TimeRegistration previousTimeRegistration = getDao().getPreviousTimeRegistration(testData.trsForTask2.get(0));
	assertNotNull(previousTimeRegistration);
	Assert.assertSameDate(testData.getDateTime(2011, 10, 22, 13, 0, 0, 0), previousTimeRegistration.getStartTime());
	Assert.assertSameDate(testData.getDateTime(2011, 10, 22, 14, 1, 30, 0), previousTimeRegistration.getEndTime());
    }

    public void testGetPreviousTimeRegistrationForFirstTimeRegistration() {
	setupDatabase();

	TimeRegistration previousTimeRegistration = getDao().getPreviousTimeRegistration(testData.trsForDefaultTask.get(0));
	assertNull(previousTimeRegistration);
    }

    public void testGetPreviousTimeRegistrationForLastTimeRegistration() {
	setupDatabase();

	TimeRegistration expectedTimeRegistration = testData.trsForTask2.get(testData.trsForTask2.size()-2);

	TimeRegistration previousTimeRegistration = getDao().getPreviousTimeRegistration(testData.trsForTask2.get(testData.trsForTask2.size()-1));
	assertNotNull(previousTimeRegistration);
	assertEquals(expectedTimeRegistration.getId(), previousTimeRegistration.getId());
    }

    public void testGetNextTimeRegistration() {
	setupDatabase();

	TimeRegistration expectedTimeRegistration = testData.trsForTask2.get(1);

	TimeRegistration nextTimeRegistration = getDao().getNextTimeRegistration(testData.trsForTask2.get(0));
	assertNotNull(nextTimeRegistration);
	assertEquals(expectedTimeRegistration.getId(), nextTimeRegistration.getId());
    }

    public void testGetNextTimeRegistrationForLastTimeRegistration() {
	setupDatabase();
	TimeRegistration nextTimeRegistration = getDao().getNextTimeRegistration(testData.trsForTask2.get(testData.trsForTask2.size()-1));
	assertNull(nextTimeRegistration);
    }

    public void testGetNextTimeRegistrationForFirstTimeRegistration() {
	setupDatabase();

	TimeRegistration expectedTimeRegistration = testData.trsForDefaultTask.get(1);

	TimeRegistration nextTimeRegistration = getDao().getNextTimeRegistration(testData.trsForDefaultTask.get(0));
	assertNotNull(nextTimeRegistration);
	assertEquals(expectedTimeRegistration.getId(), nextTimeRegistration.getId());
    }
}
