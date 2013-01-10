package eu.vranckaert.worktime.json.endpoint.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import eu.vranckaert.worktime.model.Service;
import eu.vranckaert.worktime.model.ServicePlatform;
import eu.vranckaert.worktime.security.dao.ServiceDao;
import eu.vranckaert.worktime.security.utils.KeyGenerator;

@Path("setup")
public class SetupEndpoint {
	@Inject
	private ServiceDao serviceDao;
	
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayHello() {
		return "Hello World";
	}
	
	@GET
	@Path("setupService")
	@Produces(MediaType.TEXT_PLAIN)
	public String setupService() {
		// Create a setup service...
		Service serviceTest = new Service();
		serviceTest.setAppName("WorkTime for Android");
		serviceTest.setContact("dirkvranckaert@gmail.com");
		serviceTest.setPlatform(ServicePlatform.ANDROID);
		serviceTest.setServiceKey(KeyGenerator.getNewKey());
		serviceDao.persist(serviceTest);
		
		return "Service created!";
	}
}
