package eu.vranckaert.worktime.dao.impl;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Query.FilterOperator;

import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.User;

public class ProjectDaoImpl extends BaseDaoImpl<Project> implements ProjectDao {
	public ProjectDaoImpl() {
		super(Project.class);
	}
	
	@Override
	public long persist(Project instance) {
		instance.setLastUpdated(new Date());
		return super.persist(instance);
	}

	@Override
	public Project update(Project instance) {
		instance.setLastUpdated(new Date());
		return super.update(instance);
	}

	@Override
	public List<Project> findAll(User user) {
		List<Project> projects = getDataStore().find()
				.type(Project.class)
				.ancestor(user)
				.returnAll()
				.now();
		
		// Check transaction cache
		List<Project> cachedProjects = getCachedObjects(user);
		projects.addAll(cachedProjects);
		
		return projects;
	}

	@Override
	public Project find(String name, User user) {
		try {
			Project project = getDataStore().find()
					.type(Project.class)
					.addFilter("name", FilterOperator.EQUAL, name)
					.ancestor(user)
					.returnUnique()
					.now();
			
			// Check transaction cache
			if (project == null) {
				for (Project cachedProject : getCachedObjects(user)) {
					if (cachedProject.getName().equals(name)) {
						return cachedProject;
					}
				}
			}
			
			return project;
		} catch (IllegalStateException e) {
			return null;
		}
	}

	@Override
	public Project findBySyncKey(String syncKey, User user) {
		try {
			Project project = getDataStore().find()
				.type(Project.class)
				.addFilter("syncKey", FilterOperator.EQUAL, syncKey)
				.ancestor(user)
				.returnUnique()
				.now();
			
			// Check transaction cache
			if (project == null) {
				for (Project cachedProject : getCachedObjects(user)) {
					if (cachedProject.getSyncKey().equals(syncKey)) {
						return cachedProject;
					}
				}
			}
			
			return project;
		} catch (IllegalStateException e) {
			return null;
		}
	}

	@Override
	public boolean isUniqueSynKey(String syncKey, User user) {
		int count = getDataStore().find()
				.type(Project.class)
				.addFilter("syncKey", FilterOperator.EQUAL, syncKey)
				.ancestor(user)
				.returnCount()
				.now();
		
		// Check transaction cache
		for (Project cachedProject : getCachedObjects(user)) {
			if (cachedProject.getSyncKey().equals(syncKey)) {
				count ++;
			}
		}
		
		return count == 0 ? true : false;
	}
	
	@Override
	public List<Project> findAllModifiedAfter(User user, Date lastModifiedDate) {
		List<Project> projects = getDataStore().find()
				.type(Project.class)
				.addFilter("lastUpdated", FilterOperator.GREATER_THAN_OR_EQUAL, lastModifiedDate)
				.ancestor(user)
				.returnAll()
				.now();
		return projects;
	}
}
