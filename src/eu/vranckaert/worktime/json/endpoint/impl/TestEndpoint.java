package eu.vranckaert.worktime.json.endpoint.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import eu.vranckaert.worktime.model.Role;
import eu.vranckaert.worktime.model.Service;
import eu.vranckaert.worktime.model.ServicePlatform;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.dao.ServiceDao;
import eu.vranckaert.worktime.security.dao.impl.ServiceDaoImpl;
import eu.vranckaert.worktime.security.exception.EmailAlreadyInUseException;
import eu.vranckaert.worktime.security.exception.PasswordLenghtInvalidException;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.security.service.impl.UserServiceImpl;
import eu.vranckaert.worktime.security.utils.KeyGenerator;

@Path("setup")
public class TestEndpoint {
	
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayHello() {
		return "Hello World";
	}
	
	@GET
	@Path("clearAll")
	@Produces(MediaType.TEXT_PLAIN)
	public String clearAll() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Query mydeleteq = new Query();
		PreparedQuery pq = datastore.prepare(mydeleteq);
		for (Entity result : pq.asIterable()) {
		    datastore.delete(result.getKey());      
		}
		
		return "All data has been cleared!";
	}
	
	@GET
	@Path("init")
	@Produces(MediaType.TEXT_PLAIN)
	public String init() {
		// Create a setup service...
		Service serviceTest = new Service();
		serviceTest.setAppName("WorkTime for Android");
		serviceTest.setContact("dirkvranckaert@gmail.com");
		serviceTest.setPlatform(ServicePlatform.ANDROID);
		serviceTest.setServiceKey(KeyGenerator.getNewKey());
		ServiceDao serviceDao = new ServiceDaoImpl();
		serviceDao.persist(serviceTest);
		
		User user = new User();
		user.setEmail("dirkvranckaert@gmail.com");
		user.setFirstName("Dirk");
		user.setLastName("Vranckaert");
		user.setRole(Role.ADMIN);
		UserService userService = new UserServiceImpl();
		try {
			userService.register(user, "test123");
		} catch (EmailAlreadyInUseException e) {
		} catch (PasswordLenghtInvalidException e) {
		}
		
		return "Application initialization ok!";
	}

	@GET
	@Path("reInit")
	@Produces(MediaType.TEXT_PLAIN)
	public String reInit() {
		clearAll();
		init();
		
		return "Re-initialization completed!";
	}
}
