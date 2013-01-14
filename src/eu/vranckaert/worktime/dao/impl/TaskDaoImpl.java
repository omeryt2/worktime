package eu.vranckaert.worktime.dao.impl;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Query.FilterOperator;

import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.User;

public class TaskDaoImpl extends BaseDaoImpl<Task> implements TaskDao {
	public TaskDaoImpl() {
		super(Task.class);
	}
	
	@Override
	public long persist(Task instance) {
		instance.setLastUpdated(new Date());
		return super.persist(instance);
	}

	@Override
	public Task update(Task instance) {
		instance.setLastUpdated(new Date());
		return super.update(instance);
	}

	@Override
	public List<Task> findAll(User user) {
		List<Task> tasks = getDataStore().find()
				.type(Task.class)
				.ancestor(user)
				.returnAll()
				.now();
		
		// Check transaction cache
		List<Task> cachedTasks = getCachedObjects(user);
		tasks.addAll(cachedTasks);
		
		return tasks;
	}

	@Override
	public Task find(String name, User user) {
		try {
			Task task = getDataStore().find()
					.type(Task.class)
					.addFilter("name", FilterOperator.EQUAL, name)
					.ancestor(user)
					.returnUnique()
					.now();
			
			// Check transaction cache
			if (task == null) {
				for (Task cache : getCachedObjects(user)) {
					if (cache.getName().equals(name)) {
						return cache;
					}
				}
			}
			
			return task;			
		} catch (IllegalStateException e) {
			return null;
		}
	}

	@Override
	public Task findBySyncKey(String syncKey, User user) {
		try {
			Task task = getDataStore().find()
					.type(Task.class)
					.addFilter("syncKey", FilterOperator.EQUAL, syncKey)
					.ancestor(user)
					.returnUnique()
					.now();
			
			// Check transaction cache
			if (task == null) {
				for (Task cache : getCachedObjects(user)) {
					if (cache.getSyncKey().equals(syncKey)) {
						return cache;
					}
				}
			}
			
			return task;
		} catch (IllegalStateException e) {
			return null;
		}
	}

	@Override
	public boolean isUniqueSynKey(String syncKey, User user) {
		int count = getDataStore().find()
				.type(Task.class)
				.addFilter("syncKey", FilterOperator.EQUAL, syncKey)
				.ancestor(user)
				.returnCount()
				.now();
		
		// Check transaction cache
		for (Task cache : getCachedObjects(user)) {
			if (cache.getSyncKey().equals(syncKey)) {
				count ++;
			}
		}
		
		return count == 0 ? true : false;
	}
	
	@Override
	public List<Task> findAllModifiedAfter(User user, Date lastModifiedDate) {
		List<Task> tasks = getDataStore().find()
				.type(Task.class)
				.addFilter("lastUpdated", FilterOperator.GREATER_THAN_OR_EQUAL, lastModifiedDate)
				.ancestor(user)
				.returnAll()
				.now();
		return tasks;
	}
}
