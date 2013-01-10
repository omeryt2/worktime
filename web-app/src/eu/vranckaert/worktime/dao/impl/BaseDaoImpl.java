package eu.vranckaert.worktime.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.Parent;
import com.google.inject.Inject;
import com.google.inject.Provider;

import eu.vranckaert.worktime.dao.BaseDao;

public class BaseDaoImpl <T> implements BaseDao <T> {
	@Inject
	private Provider<ObjectDatastore> dataStores;
	
	private List<T> transactionCache;
	
	private Class<T> clazz;
	
	public BaseDaoImpl(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public ObjectDatastore getDataStore() {
		ObjectDatastore datastore = dataStores.get();
		if (datastore.getTransaction() == null || !datastore.getTransaction().isActive()) {
			transactionCache = null;
		} else if (transactionCache == null) {
			transactionCache = new ArrayList<T>();
		}
		return datastore;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return getDataStore().find().type(clazz).returnAll().now();
	}
	
	public long persist(T instance) {
		Key key = getDataStore().store(instance);
		
		if (getDataStore().getTransaction() != null && getDataStore().getTransaction().isActive()) {
			transactionCache.add(instance);
		}
		
		return key.getId();
	}
	
	public T update(T instance) {
		getDataStore().update(instance);
		return instance;
	}
	
	public void remove(T instance) {
		getDataStore().delete(instance);
	}
	
	@SuppressWarnings("unchecked")
	public T findById(Object id) {
		return (T) getDataStore().load(clazz, id);
	}
	
	public void refresh(T instance) {
		getDataStore().refresh(instance);
	}
	
	private boolean useTransactionCache() {
		if (getDataStore().getTransaction() != null && getDataStore().getTransaction().isActive()) {
			return true;
		}
		return false;
	}
	
	@Deprecated
	public List<T> getCachedObjects() {
		if (!useTransactionCache()) {
			return new ArrayList<T>();
		}
		return transactionCache;
	}
	
	public List<T> getCachedObjects(Object ancestor) {
		if (!useTransactionCache()) {
			return new ArrayList<T>();
		}
		
		List<T> subList = new ArrayList<T>();
		
		for (T cachedObject : transactionCache) {
			if (hasAncestorInTree(cachedObject, ancestor)) {
				subList.add(cachedObject);
			}
		}
		
		return subList;
	}
	
	private boolean hasAncestorInTree(Object object, Object ancestor) {
		Class<? extends Object> clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field ancestorField : fields) {
			ancestorField.setAccessible(true);
			if (ancestorField.getAnnotation(Parent.class) != null) {
				try {
					Object ancestorFieldValue = ancestorField.get(object);
					if (ancestorFieldValue.getClass().isInstance(ancestor)) {
						if (ancestorFieldValue.equals(ancestor)) {
							return true;
						}
						return false;
					} else {
						return hasAncestorInTree(ancestorFieldValue, ancestor);
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		
		return false;
	}
}
