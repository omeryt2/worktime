package eu.vranckaert.worktime.dao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Key;
import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.annotation.Parent;
import com.google.inject.Inject;
import com.google.inject.Provider;

import eu.vranckaert.worktime.dao.BaseDao;

public class BaseDaoImpl <T> implements BaseDao <T> {
	private static final Logger log = Logger.getLogger(BaseDaoImpl.class.getName());
	
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
			log.info("Not using the transaction cache...");
			return new ArrayList<T>();
		}
		
		List<T> subList = new ArrayList<T>();
		
		log.info("Looking in to the transaction cache...");
		log.info("Transaction cache is null?" + ( transactionCache == null ? "Yes" : "No" ) );
		for (T cachedObject : transactionCache) {
			log.info("Checking for ancestor in tree");
			if (hasAncestorInTree(cachedObject, ancestor)) {
				log.info("Cached object found...");
				subList.add(cachedObject);
			}
		}
		
		return subList;
	}
	
	private boolean hasAncestorInTree(Object object, Object ancestor) {
		Class<? extends Object> clazz = object.getClass();
		
		log.info("Checking for object " + clazz.getSimpleName());
		
		Field[] fields = clazz.getDeclaredFields();
		for (Field ancestorField : fields) {
			log.info("Field: " + ancestorField.getName());
			ancestorField.setAccessible(true);
			if (ancestorField.getAnnotation(Parent.class) != null) {
				log.info("Field has @Parent annotation");
				try {
					log.info("Trying to get the value of the field...");
					Object ancestorFieldValue = ancestorField.get(object);
					log.info("Value of field: " + ancestorFieldValue);
					log.info("Checking if the field-value is an instance of " + ancestor.getClass().getSimpleName());
					if (ancestorFieldValue != null && ancestorFieldValue.getClass().isInstance(ancestor)) {
						log.info("The field value is an instance. Checking the equals of the field-value and the ancestor.");
						if (ancestorFieldValue.equals(ancestor)) {
							log.info("The value is an instance of the ancestor");
							return true;
						}
						log.info("The value is not an instance of the ancestor");
						return false;
					} else if (ancestorFieldValue != null) {
						log.info("The field value is not an instance of the ancestor. Continuing to check the fields of the field...");
						return hasAncestorInTree(ancestorFieldValue, ancestor);
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		
		log.info("Ancestor not found in tree...");
		return false;
	}
}
