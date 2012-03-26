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
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.test.cases.DaoTestCase;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 26/03/12
 * Time: 14:03
 */
public class TaskDaoTest extends DaoTestCase<TaskDao, TaskDaoImpl> {
    int finishedTasksForProject = 9;
    int unfinishedTasksForProject = 4;
    
    public TaskDaoTest() {
        super(TaskDaoImpl.class);
    }

    private Project setupDatabase() {
        ProjectDao projectDao = getDaoForClass(ProjectDao.class, ProjectDaoImpl.class);
        
        Project project = new Project();
        project.setName("DAO TEST");
        project.setFinished(false);
        project.setDefaultValue(false);
        project = projectDao.save(project);

        for (int i=0; i<finishedTasksForProject; i++) {
            Task task = new Task();
            task.setName("TEST FINISHED " + i);
            task.setFinished(true);
            task.setProject(project);
            getDao().save(task);
        }

        for (int i=0; i<unfinishedTasksForProject; i++) {
            Task task = new Task();
            task.setName("TEST UNFINISHED " + i);
            task.setFinished(false);
            task.setProject(project);
            getDao().save(task);
        }
        
        return project;
    }

    public void testFindTasksForProject() {
        Project project = setupDatabase();

        List<Task> tasks = getDao().findTasksForProject(project);

        assertEquals(finishedTasksForProject + unfinishedTasksForProject, tasks.size());
    }

    public void testFindNotFinishedTasksForProject() {
        Project project = setupDatabase();

        List<Task> tasks = getDao().findNotFinishedTasksForProject(project);

        assertEquals(unfinishedTasksForProject, tasks.size());
    }
}
