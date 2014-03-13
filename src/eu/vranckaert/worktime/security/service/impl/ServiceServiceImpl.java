package eu.vranckaert.worktime.security.service.impl;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

import eu.vranckaert.worktime.model.Service;
import eu.vranckaert.worktime.model.ServicePlatform;
import eu.vranckaert.worktime.security.dao.ServiceDao;
import eu.vranckaert.worktime.security.service.ServiceService;
import eu.vranckaert.worktime.security.utils.KeyGenerator;

public class ServiceServiceImpl implements ServiceService {
	@Inject
	private ServiceDao serviceDao;
	
	@Override
	public boolean isServiceAllowed(String key) {
		if (StringUtils.isBlank(key)) {
			return false;
		}
		return serviceDao.isServiceAllowed(key);
	}

	@Override
	public String createService(String appName, String contact,
			ServicePlatform platform) {
		String serviceKey = KeyGenerator.getNewKey();
		
		Service service = new Service();
		service.setAppName(appName);
		service.setContact(contact);
		service.setPlatform(platform);
		service.setServiceKey(serviceKey);
		
		serviceDao.persist(service);
		
		return serviceKey;
	}

	@Override
	public void removeService(String serviceKey) {
		Service service = serviceDao.findById(serviceKey);
		if (service != null) {
			serviceDao.remove(service);
		}
	}

}
