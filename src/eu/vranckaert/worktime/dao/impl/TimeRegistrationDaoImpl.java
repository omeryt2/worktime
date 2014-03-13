package eu.vranckaert.worktime.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Query.FilterOperator;

import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.User;

public class TimeRegistrationDaoImpl extends BaseDaoImpl<TimeRegistration> implements TimeRegistrationDao {
	private static final Logger log = Logger.getLogger(TimeRegistrationDaoImpl.class.getName());
	
	public TimeRegistrationDaoImpl() {
		super(TimeRegistration.class);
	}
	
	@Override
	public long persist(TimeRegistration instance) {
		instance.setLastUpdated(new Date());
		return super.persist(instance);
	}

	@Override
	public TimeRegistration update(TimeRegistration instance) {
		instance.setLastUpdated(new Date());
		return super.update(instance);
	}
	
	@Override
	public List<TimeRegistration> findAll(User user) {
		List<TimeRegistration> timeRegistrationResult = getDataStore().find()
				.type(TimeRegistration.class)
				.ancestor(user)
				.returnAll()
				.now();
		
		// Check transaction cache
		List<TimeRegistration> cachedTimeRegistrations = getCachedObjects(user);
		timeRegistrationResult.addAll(cachedTimeRegistrations);
		
		return timeRegistrationResult;
	}
	
	@Override
	public TimeRegistration find(Date startTime, Date endTime, User user) {
		log.info("Lookinf for a time registration that starts on " + startTime + " and ends on " + endTime);
		if (endTime != null) {
			log.info("The end time is not null...");
			try {
				TimeRegistration timeRegistration = getDataStore().find()
						.type(TimeRegistration.class)
						.addFilter("startTime", FilterOperator.EQUAL, startTime)
						.addFilter("endTime", FilterOperator.EQUAL, endTime)
						.ancestor(user)
						.returnUnique()
						.now();
				
				log.info("Query for time registration has been executed...");
				
				// Check transaction cache
				if (timeRegistration == null) {
					log.info("But no time registration has been found. Will check the cached objects now");
					for (TimeRegistration cache : getCachedObjects(user)) {
						if (cache.getStartTime().equals(startTime)
								&& cache.getEndTime().equals(endTime)) {
							log.info("Time registration foudn in cache!");
							return cache;
						}
					}
				}
				
				return timeRegistration;
			} catch (IllegalStateException e) {
				log.info("Illegal State Exception: " + e.getMessage());
				e.printStackTrace();
				return null;
			}
		} else {
			log.info("The end time is null...");
			try {
				TimeRegistration timeRegistration = getDataStore().find()
						.type(TimeRegistration.class)
						.addFilter("startTime", FilterOperator.EQUAL, startTime)
						.ancestor(user)
						.returnUnique()
						.now();
				
				log.info("Query for time registration has been executed...");
				
				// Check transaction cache
				if (timeRegistration == null) {
					log.info("But no time registration has been found. Will check the cached objects now");
					for (TimeRegistration cache : getCachedObjects(user)) {
						if (cache.getStartTime().equals(startTime)
								&& cache.getEndTime() == null) {
							log.info("Time registration foudn in cache!");
							return cache;
						}
					}
				}
				
				return timeRegistration;
			} catch (IllegalStateException e) {
				log.info("Illegal State Exception: " + e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
	}
	
	@Override
	public TimeRegistration findBySyncKey(String syncKey, User user) {
		TimeRegistration timeRegistration = getDataStore().find()
				.type(TimeRegistration.class)
				.addFilter("syncKey", FilterOperator.EQUAL, syncKey)
				.ancestor(user)
				.returnUnique()
				.now();
		
		// Check transaction cache
		if (timeRegistration == null) {
			for (TimeRegistration cache : getCachedObjects(user)) {
				if (cache.getSyncKey().equals(syncKey)) {
					return cache;
				}
			}
		}
		
		return timeRegistration;
	}
	
	@Override
	public List<TimeRegistration> findInterferingTimeRegistrations(TimeRegistration timeRegistration, User user) {
		TimeRegistration timeRegistrationCopy = timeRegistration.clone();
		if (timeRegistrationCopy.getEndTime() == null)
			timeRegistrationCopy.setEndTime(new Date());
		
		List<TimeRegistration> allTimeRegistrations = findAll(user);
		List<TimeRegistration> interferingTimeRegistrations = new ArrayList<TimeRegistration>();
		
		for (TimeRegistration tr : allTimeRegistrations) {
			if (tr.isOngoingTimeRegistration()) {
				if (timeRegistrationCopy.getStartTime().getTime() >= tr.getStartTime().getTime()) {
					interferingTimeRegistrations.add(tr);
				}
			} else {
				if ( (tr.getStartTime().getTime() >= timeRegistrationCopy.getStartTime().getTime() && tr.getStartTime().getTime() < timeRegistrationCopy.getEndTime().getTime()) 
						|| (tr.getEndTime().getTime() > timeRegistrationCopy.getStartTime().getTime() && tr.getEndTime().getTime() <= timeRegistrationCopy.getEndTime().getTime()) ) {
					interferingTimeRegistrations.add(tr);
				}
			}
		}
		
		return interferingTimeRegistrations;
	}
	
	@Override
	public boolean isUniqueSynKey(String syncKey, User user) {
		int count = getDataStore().find()
				.type(TimeRegistration.class)
				.addFilter("syncKey", FilterOperator.EQUAL, syncKey)
				.ancestor(user)
				.returnCount()
				.now();
		
		// Check transaction cache
		for (TimeRegistration cache : getCachedObjects(user)) {
			if (cache.getSyncKey().equals(syncKey)) {
				count ++;
			}
		}
		
		return count == 0 ? true : false;
	}
	
	@Override
	public List<TimeRegistration> findAllModifiedAfter(User user, Date lastModifiedDate) {
		List<TimeRegistration> timeRegistrations = getDataStore().find()
				.type(TimeRegistration.class)
				.addFilter("lastUpdated", FilterOperator.GREATER_THAN_OR_EQUAL, lastModifiedDate)
				.ancestor(user)
				.returnAll()
				.now();
		return timeRegistrations;
	}
	
	@Override
	public TimeRegistration findOngoingTimeRegistration(User user) {		
		List<TimeRegistration> allTimeRegistrations = findAll(user);
		for (TimeRegistration timeRegistration : allTimeRegistrations) {
			if (timeRegistration.isOngoingTimeRegistration()) {
				return timeRegistration;
			}
		}
		return null;
	}
}
