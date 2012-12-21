package eu.vranckaert.worktime.dao;

import com.google.appengine.api.datastore.Transaction;
import com.vercer.engine.persist.ObjectDatastore;
import com.vercer.engine.persist.annotation.AnnotationObjectDatastore;

public class StaticDataStore {
	private static ObjectDatastore dataStore;
	
	public static ObjectDatastore getDataStore() {
		if (dataStore == null || dataStore.getTransaction() == null || !dataStore.getTransaction().isActive()) {
			dataStore = new AnnotationObjectDatastore();
		}
		return dataStore;
	}
	
	public static Transaction getTransaction() {
		return dataStore.beginTransaction();
	}
}
