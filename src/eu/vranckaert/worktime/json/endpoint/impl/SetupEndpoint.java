package eu.vranckaert.worktime.json.endpoint.impl;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.inject.Inject;

import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.json.base.request.RegisteredServiceRequest;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Service;
import eu.vranckaert.worktime.model.ServicePlatform;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.dao.ServiceDao;
import eu.vranckaert.worktime.security.dao.UserDao;
import eu.vranckaert.worktime.security.exception.ServiceNotAllowedException;
import eu.vranckaert.worktime.security.service.SecurityChecker;
import eu.vranckaert.worktime.security.utils.KeyGenerator;

@Path("setup")
public class SetupEndpoint {
	@Inject
	private SecurityChecker securityChecker;
	
	@Inject
	private ServiceDao serviceDao;
	
	@Inject
	private ProjectDao projectDao;
	
	@Inject
	private TaskDao taskDao;
	
	@Inject
	private TimeRegistrationDao timeRegistrationDao;
	
	@Inject
	private UserDao userDao;
	
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayHello() {
		return "Hello World";
	}
	
//	@GET
//	@Path("setupService")
//	@Produces(MediaType.TEXT_PLAIN)
//	public String setupService() {
//		// Create a setup service...
//		Service serviceTest = new Service();
//		serviceTest.setAppName("WorkTime for Android");
//		serviceTest.setContact("dirkvranckaert@gmail.com");
//		serviceTest.setPlatform(ServicePlatform.ANDROID);
//		serviceTest.setServiceKey(KeyGenerator.getNewKey());
//		serviceDao.persist(serviceTest);
//		
//		return "Service created!";
//	}
	
	@GET
	@Path("removeProjectTasksTimeRegistrationsForUser")
	@Produces(MediaType.TEXT_PLAIN)
	public String retrieveIdsForUser(@QueryParam("serviceKey") String serviceKey, @QueryParam("email") String email) {
		RegisteredServiceRequest request = new RegisteredServiceRequest() {};
		request.setServiceKey(serviceKey);
		
		try {
			securityChecker.checkService(request);
		} catch (ServiceNotAllowedException e) {
			return "Not Done...";
		}
		
		User user = userDao.findById(email);
		
		List<Project> projects = projectDao.findAll(user);
		List<Task> tasks = taskDao.findAll(user);
		List<TimeRegistration> timeRegistrations = timeRegistrationDao.findAll(user);
		
		for (TimeRegistration timeRegistration : timeRegistrations) {
			timeRegistrationDao.remove(timeRegistration);
		}
		
		for (Task task : tasks) {
			taskDao.remove(task);
		}
		
		for (Project project : projects) {
			projectDao.remove(project);
		}
		
		return "Done!";
	}
}
