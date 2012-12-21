package eu.vranckaert.worktime.guice;

import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

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

public class GuiceModule extends AbstractModule {
	Logger logger = Logger.getLogger(GuiceModule.class.getName());
	
	@Override
	public void configure() {
		logger.info("Configuring DI");
		bindSecurity();
		bindDaos();
		bindServices();
	}
	
	private void bindSecurity() {
		logger.info("Binding Security...");
		// DAO's
		bind(UserDao.class).to(UserDaoImpl.class).in(Singleton.class);
		bind(ServiceDao.class).to(ServiceDaoImpl.class).in(Singleton.class);
		bind(SessionDao.class).to(SessionDaoImpl.class).in(Singleton.class);
		bind(PasswordResetRequestDao.class).to(PasswordResetRequestDaoImpl.class);
		// Services
		bind(UserService.class).to(UserServiceImpl.class).in(Singleton.class);
		bind(ServiceService.class).to(ServiceServiceImpl.class).in(Singleton.class);
		bind(SecurityChecker.class).to(SecurityCheckerImpl.class).in(Singleton.class);
		logger.info("All security services and DAO's are now bound...");
	}
	
	private void bindDaos() {
		logger.info("Binding DAO's...");
		logger.info("All DAO's are now bound...");
	}
	
	private void bindServices() {
		logger.info("Binding services...");
		logger.info("All services are now bound...");
	}
}
