package eu.vranckaert.worktime.security.dao.impl;

import eu.vranckaert.worktime.dao.impl.BaseDaoImpl;
import eu.vranckaert.worktime.model.Service;
import eu.vranckaert.worktime.security.dao.ServiceDao;

public class ServiceDaoImpl extends BaseDaoImpl<Service> implements ServiceDao {

	public ServiceDaoImpl() {
		super(Service.class);
	}

	@Override
	public boolean isServiceAllowed(String key) {
		Service service = getDataStore().load(Service.class, key);
		return service == null ? false : true;
	}

}
