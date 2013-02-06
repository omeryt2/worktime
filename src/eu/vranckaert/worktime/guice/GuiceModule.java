package eu.vranckaert.worktime.guice;

import java.util.logging.Logger;

import com.google.code.twig.ObjectDatastore;
import com.google.code.twig.ObjectDatastoreFactory;
import com.google.code.twig.annotation.AnnotationObjectDatastore;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;

import eu.vranckaert.worktime.dao.ProjectDao;
import eu.vranckaert.worktime.dao.SyncHistoryDao;
import eu.vranckaert.worktime.dao.TaskDao;
import eu.vranckaert.worktime.dao.TimeRegistrationDao;
import eu.vranckaert.worktime.dao.impl.ProjectDaoImpl;
import eu.vranckaert.worktime.dao.impl.SyncHistoryDaoImpl;
import eu.vranckaert.worktime.dao.impl.TaskDaoImpl;
import eu.vranckaert.worktime.dao.impl.TimeRegistrationDaoImpl;
import eu.vranckaert.worktime.model.PasswordResetRequest;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Service;
import eu.vranckaert.worktime.model.Session;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.model.sync.SyncHistory;
import eu.vranckaert.worktime.security.dao.PasswordResetRequestDao;
import eu.vranckaert.worktime.security.dao.ServiceDao;
import eu.vranckaert.worktime.security.dao.SessionDao;
import eu.vranckaert.worktime.security.dao.UserDao;
import eu.vranckaert.worktime.security.dao.impl.PasswordResetRequestDaoImpl;
import eu.vranckaert.worktime.security.dao.impl.ServiceDaoImpl;
import eu.vranckaert.worktime.security.dao.impl.SessionDaoImpl;
import eu.vranckaert.worktime.security.dao.impl.UserDaoImpl;
import eu.vranckaert.worktime.security.service.SecurityChecker;
import eu.vranckaert.worktime.security.service.ServiceService;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.security.service.impl.SecurityCheckerImpl;
import eu.vranckaert.worktime.security.service.impl.ServiceServiceImpl;
import eu.vranckaert.worktime.security.service.impl.UserServiceImpl;
import eu.vranckaert.worktime.service.SyncService;
import eu.vranckaert.worktime.service.impl.SyncServiceImpl;

public class GuiceModule extends AbstractModule {
	Logger logger = Logger.getLogger(GuiceModule.class.getName());
	
	@Override
	public void configure() {
		logger.info("Configuring twig-persist");
		configureEntities();
		logger.info("Configuring DI");
		bindSecurity();
		bindDaos();
		bindServices();
	}
	
	private void configureEntities() {
		bind(ObjectDatastore.class).to(AnnotationObjectDatastore.class).in(RequestScoped.class);
		
		// Security / User management
		ObjectDatastoreFactory.register(Service.class);
		ObjectDatastoreFactory.register(User.class);
		ObjectDatastoreFactory.register(Session.class);
		ObjectDatastoreFactory.register(PasswordResetRequest.class);
		
		// Core model
		ObjectDatastoreFactory.register(Project.class);
		ObjectDatastoreFactory.register(Task.class);
		ObjectDatastoreFactory.register(TimeRegistration.class);
		
		// Sync
		ObjectDatastoreFactory.register(SyncHistory.class);
	}
	
	private void bindSecurity() {
		logger.info("Binding Security...");
		// DAO's
		bind(UserDao.class).to(UserDaoImpl.class).in(RequestScoped.class);
		bind(ServiceDao.class).to(ServiceDaoImpl.class).in(RequestScoped.class);
		bind(SessionDao.class).to(SessionDaoImpl.class).in(RequestScoped.class);
		bind(PasswordResetRequestDao.class).to(PasswordResetRequestDaoImpl.class).in(RequestScoped.class);
		// Services
		bind(UserService.class).to(UserServiceImpl.class).in(RequestScoped.class);
		bind(ServiceService.class).to(ServiceServiceImpl.class).in(RequestScoped.class);
		bind(SecurityChecker.class).to(SecurityCheckerImpl.class).in(RequestScoped.class);
		logger.info("All security services and DAO's are now bound...");
	}
	
	private void bindDaos() {
		logger.info("Binding DAO's...");
		bind(SyncHistoryDao.class).to(SyncHistoryDaoImpl.class).in(RequestScoped.class);
		bind(ProjectDao.class).to(ProjectDaoImpl.class).in(RequestScoped.class);
		bind(TaskDao.class).to(TaskDaoImpl.class).in(RequestScoped.class);
		bind(TimeRegistrationDao.class).to(TimeRegistrationDaoImpl.class).in(RequestScoped.class);
		logger.info("All DAO's are now bound...");
	}
	
	private void bindServices() {
		logger.info("Binding services...");
		bind(SyncService.class).to(SyncServiceImpl.class).in(RequestScoped.class);
		logger.info("All services are now bound...");
	}
}
