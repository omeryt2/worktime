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

package eu.vranckaert.worktime.dao;

import eu.vranckaert.worktime.R;
import eu.vranckaert.worktime.dao.impl.ProjectDaoImpl;
import eu.vranckaert.worktime.exceptions.CorruptProjectDataException;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.test.cases.DaoTestCase;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 26/03/12
 * Time: 13:26
 */
public class ProjectDaoTest extends DaoTestCase<ProjectDao, ProjectDaoImpl> {
    public ProjectDaoTest() {
        super(ProjectDaoImpl.class);
    }

    private Project getDummyProject() {
        Project project = new Project();
        project.setComment("DAO TEST");
        project.setFinished(false);

        return project;
    }

    public void testIsNameAlreadyUsed() {
        String projectName1 = "projectName1";
        String projectName2 = "projectName2";
        String projectName3 = "projectName3";

        Project project1 = getDummyProject() ;
        project1.setName(projectName1);

        Project project3 = getDummyProject() ;
        project3.setName(projectName3);
        
        getDao().save(project1);
        getDao().save(project3);
        
        boolean result1 = getDao().isNameAlreadyUsed(projectName1);
        boolean result2 = getDao().isNameAlreadyUsed(projectName2);
        boolean result3 = getDao().isNameAlreadyUsed(projectName3);
        
        assertTrue(result1);
        assertFalse(result2);
        assertTrue(result3);
    }
    
    public void testCountTotalNumberOfProjects() {
        assertEquals("1 project should have been found!", 1, getDao().countTotalNumberOfProjects());
        
        getDao().save(getDummyProject());
        getDao().save(getDummyProject());

        assertEquals("3 projects should have been found!", 3, getDao().countTotalNumberOfProjects());
        
        getDao().save(getDummyProject());
        
        assertEquals("3 projects should have been found!", 4, getDao().countTotalNumberOfProjects());
        
        getDao().save(getDummyProject());
        
        assertEquals("3 projects should have been found!", 5, getDao().countTotalNumberOfProjects());
    }
    
    public void testFindDefaultProject() {
        String newDefaultProjectName = "Test new default project!";
        
        Project project1 = getDao().findDefaultProject();
        assertTrue(project1.isDefaultValue());
        assertEquals(ctx.getString(R.string.default_project_name), project1.getName());
        
        project1.setDefaultValue(false);
        getDao().update(project1);
        
        Project project2 = getDummyProject();
        project2.setName(newDefaultProjectName);
        project2.setDefaultValue(true);
        getDao().save(project2);

        Project project3 = getDao().findDefaultProject();
        assertTrue(project3.isDefaultValue());
        assertEquals(newDefaultProjectName, project3.getName());
    }
    
    public void testFindDefaultProjectFailure() {
        List<Project> projects = getDao().findAll();
        for (Project project : projects) {
            project.setDefaultValue(false);
            getDao().update(project);
        }
        
        try {
            getDao().findDefaultProject();
            fail();
        } catch (CorruptProjectDataException e) {
            assertNotNull(e);
        }
    }
    
    public void testFindProjectsOnFinishedFlag() {
        int finishedProjectsCount = 15;
        int unfinishedProjectsCount = 22;
        
        List<Project> projects = getDao().findAll();
        for (Project project : projects) {
            getDao().delete(project);
        }

        for (int i=0; i<finishedProjectsCount; i++) {
            Project project = getDummyProject();
            project.setFinished(true);
            getDao().save(project);
        }

        for (int i=0; i<unfinishedProjectsCount; i++) {
            Project project = getDummyProject();
            project.setFinished(false);
            getDao().save(project);
        }
        
        List<Project> finishedProjects = getDao().findProjectsOnFinishedFlag(true);
        List<Project> unfinishedProjects = getDao().findProjectsOnFinishedFlag(false);

        assertEquals(finishedProjectsCount, finishedProjects.size());
        assertEquals(unfinishedProjectsCount, unfinishedProjects.size());
    }
}
