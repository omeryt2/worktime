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
package eu.vranckaert.worktime.test.data;

import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 26/03/12
 * Time: 14:58
 */
public class TimeRegistrationTestData {
    public Task defaultTask;
    public Task task1;
    public Task task2;
    public Task task3;

    public List<TimeRegistration> allTimeRegistrations = new ArrayList<TimeRegistration>();
    public List<TimeRegistration> trsForDefaultTask = new ArrayList<TimeRegistration>();
    public List<TimeRegistration> trsForTask1 = new ArrayList<TimeRegistration>();
    public List<TimeRegistration> trsForTask2 = new ArrayList<TimeRegistration>();
    public List<TimeRegistration> trsForTask3 = new ArrayList<TimeRegistration>();

    private TimeRegistrationTestData(TimeRegistrationDao trDao, ProjectDao projectDao, TaskDao taskDao) {
        createTimeRegistrations(trDao, projectDao, taskDao);
    }
    
    public static TimeRegistrationTestData getInstance(TimeRegistrationDao trDao, ProjectDao projectDao, TaskDao taskDao) {
        return new TimeRegistrationTestData(trDao, projectDao, taskDao);
    }
    
    public Date getDateTime(int year, int month, int day, int hour, int minute, int second, int miliseconds) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, miliseconds);
        return cal.getTime();
    }

    public void createTimeRegistrations(TimeRegistrationDao trDao, ProjectDao projectDao, TaskDao taskDao) {
        // Default Task
        defaultTask = taskDao.findAll().get(0);

        // Project and Task 1
        Project project1 = new Project();
        project1.setName("DAO TEST");
        project1 = projectDao.save(project1);

        task1 = new Task();
        task1.setName("DAO TEST");
        task1.setProject(project1);
        task1 = taskDao.save(task1);

        // Project and Task 2
        Project project2 = new Project();
        project2.setName("DAO TEST");
        project2 = projectDao.save(project2);

        task2 = new Task();
        task2.setName("DAO TEST");
        task2.setProject(project2);
        task2 = taskDao.save(task2);
        
        // Project and Task 3
        Project project3 = new Project();
        project3.setName("DAO TEST");
        project3 = projectDao.save(project3);

        task3 = new Task();
        task3.setName("DAO TEST");
        task3.setProject(project3);
        task3 = taskDao.save(task3);

        // create 2 TR's for default task
        TimeRegistration tr0DefaultTask = new TimeRegistration();
        tr0DefaultTask.setTask(defaultTask);
        tr0DefaultTask.setStartTime(getDateTime(2011, 10, 22, 9, 54, 0, 0));
        tr0DefaultTask.setEndTime(getDateTime(2011, 10, 22, 10, 11, 0, 0));
        trsForDefaultTask.add(trDao.save(tr0DefaultTask));

        TimeRegistration tr1DefaultTask = new TimeRegistration();
        tr1DefaultTask.setTask(defaultTask);
        tr1DefaultTask.setStartTime(getDateTime(2011, 10, 22, 10, 11, 0, 0));
        tr1DefaultTask.setEndTime(getDateTime(2011, 10, 22, 12, 0, 0, 0));
        trsForDefaultTask.add(trDao.save(tr1DefaultTask));

        // create 1 TR for task1
        TimeRegistration tr0Task1 = new TimeRegistration();
        tr0Task1.setTask(task1);
        tr0Task1.setStartTime(getDateTime(2011, 10, 22, 13, 0, 0, 0));
        tr0Task1.setEndTime(getDateTime(2011, 10, 22, 14, 1, 30, 0));
        trsForTask1.add(trDao.save(tr0Task1));

        // create 3 TR's for task2
        TimeRegistration tr0Task2 = new TimeRegistration();
        tr0Task2.setTask(task2);
        tr0Task2.setStartTime(getDateTime(2011, 10, 22, 14, 1, 30, 0));
        tr0Task2.setEndTime(getDateTime(2011, 10, 22, 16, 0, 30, 0));
        trsForTask2.add(trDao.save(tr0Task2));

        TimeRegistration tr1Task2 = new TimeRegistration();
        tr1Task2.setTask(task2);
        tr1Task2.setStartTime(getDateTime(2011, 10, 23, 8, 0, 0, 0));
        tr1Task2.setEndTime(getDateTime(2011, 10, 23, 12, 0, 0, 0));
        trsForTask2.add(trDao.save(tr1Task2));

        TimeRegistration tr2Task2 = new TimeRegistration();
        tr2Task2.setTask(task2);
        tr2Task2.setStartTime(getDateTime(2011, 10, 23, 12, 30, 30, 0));
        tr2Task2.setEndTime(getDateTime(2011, 10, 23, 16, 0, 30, 0));
        trsForTask2.add(trDao.save(tr2Task2));
        
        allTimeRegistrations.addAll(trsForDefaultTask);
        allTimeRegistrations.addAll(trsForTask1);
        allTimeRegistrations.addAll(trsForTask2);
    }
}
