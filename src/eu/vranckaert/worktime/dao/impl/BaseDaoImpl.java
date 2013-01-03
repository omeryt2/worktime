package eu.vranckaert.worktime.dao.impl;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.common.collect.Lists;
import com.vercer.engine.persist.ObjectDatastore;

import eu.vranckaert.worktime.dao.BaseDao;
import eu.vranckaert.worktime.dao.StaticDataStore;

public class BaseDaoImpl <T> implements BaseDao <T> {
	Class<T> clazz;
	ObjectDatastore dataStore;
	
	public BaseDaoImpl(Class<T> clazz) {
		this.clazz = clazz;
		dataStore = StaticDataStore.getDataStore();
	}
	
	public ObjectDatastore getDataStore() {
		return dataStore;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return (List<T>) Lists.newArrayList(
				getDataStore().find().type(clazz).returnResultsNow()
		);
	}
	
	public long persist(T instance) {
		Key key = dataStore.store(instance);
		return key.getId();
	}
	
	public T update(T instance) {
		dataStore.update(instance);
		return instance;
	}
	
	public void remove(T instance) {
		dataStore.delete(instance);
	}
	
	@SuppressWarnings("unchecked")
	public T findById(Object id) {
		return (T) dataStore.load(clazz, id);
	}
}
