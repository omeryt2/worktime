package eu.vranckaert.worktime.security.dao;

import eu.vranckaert.worktime.dao.BaseDao;
import eu.vranckaert.worktime.model.Service;

public interface ServiceDao extends BaseDao<Service> {
	boolean isServiceAllowed(String key);
}
