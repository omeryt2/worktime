package eu.vranckaert.worktime.security.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Transaction;
import com.google.code.twig.ObjectDatastore;
import com.google.inject.Inject;
import com.google.inject.Provider;

import eu.vranckaert.worktime.model.PasswordResetRequest;
import eu.vranckaert.worktime.model.Role;
import eu.vranckaert.worktime.model.Session;
import eu.vranckaert.worktime.model.Session.Platform;
import eu.vranckaert.worktime.model.User;
import eu.vranckaert.worktime.security.dao.PasswordResetRequestDao;
import eu.vranckaert.worktime.security.dao.SessionDao;
import eu.vranckaert.worktime.security.dao.UserDao;
import eu.vranckaert.worktime.security.exception.EmailAlreadyInUseException;
import eu.vranckaert.worktime.security.exception.InvalidPasswordResetKeyException;
import eu.vranckaert.worktime.security.exception.PasswordIncorrectException;
import eu.vranckaert.worktime.security.exception.PasswordLenghtInvalidException;
import eu.vranckaert.worktime.security.exception.PasswordResetKeyAlreadyUsedException;
import eu.vranckaert.worktime.security.exception.PasswordResetKeyExpiredException;
import eu.vranckaert.worktime.security.exception.UserNotFoundException;
import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.security.utils.KeyGenerator;
import eu.vranckaert.worktime.security.utils.Password;
import eu.vranckaert.worktime.util.EmailUtil;

public class UserServiceImpl implements UserService {
	private static final Logger log = Logger.getLogger(UserService.class.getName());
	
	@Inject
	private UserDao userDao;
	@Inject
	private SessionDao sessionDao;
	@Inject
	private PasswordResetRequestDao passwordResetRequestDao;
	@Inject
	private Provider<ObjectDatastore> datastores;

	@Override
	public String register(User user, String password, Platform platform) throws EmailAlreadyInUseException, PasswordLenghtInvalidException {
		user.setEmail(user.getEmail().toLowerCase());
		
		// Check for duplicate users (email address should be unique!)
		if (userDao.isEmailAlreadyInUse(user.getEmail())) {
			throw new EmailAlreadyInUseException();
		}
		
		Password.validatePassword(password);
		
		// Hash password
		user.setPasswordHash(Password.getSaltedHash(password));
		
		// Generate a session key (for immediate login)
		String sessionKey = KeyGenerator.getNewKey();
		user.addSessionKey(sessionKey, platform);
		
		// Set date fields
		user.setRegistrationDate(new Date());
		user.setLastLoginDate(new Date());
		
		// Persist user
		userDao.persist(user);
		
		return sessionKey;
	}

	@Override
	public String login(String email, String password, Platform platform) 
			throws UserNotFoundException, PasswordIncorrectException {
		email = email.toLowerCase();
		
		// Retrieve the user
		User user = userDao.findById(email);
		if (user == null) {
			throw new UserNotFoundException();
		}
		
		// Check the password
		boolean passwordCheck = Password.check(password, user.getPasswordHash());
		
		if (!passwordCheck) {
			throw new PasswordIncorrectException();
		} else {
			String sessionKey = KeyGenerator.getNewKey();
			user.addSessionKey(sessionKey, platform);
			
			// Update last login date
			user.setLastLoginDate(new Date());
			
			userDao.update(user);
			
			return sessionKey;
		}
	}
	
	@Override
	public String changePassword(String email, String oldPassword, String newPassword, Platform platform) 
			throws UserNotFoundException, PasswordIncorrectException {
		// Retrieve the user
		User user = userDao.findById(email);
		if (user == null) {
			throw new UserNotFoundException();
		}
		
		// Check the password
		boolean passwordCheck = Password.check(oldPassword, user.getPasswordHash());
		
		if (!passwordCheck) {
			throw new PasswordIncorrectException();
		} else {
			user.setPasswordHash(Password.getSaltedHash(newPassword));
			String sessionKey = KeyGenerator.getNewKey();
			
			sessionDao.removeAllSessions(user);
			user.getSessions().clear();
			
			user.addSessionKey(sessionKey, platform);
			userDao.update(user);
			
			return sessionKey;
		}
	}

	@Override
	public boolean isLoggedIn(String email, String sessionKey) {
		if (StringUtils.isBlank(email)) {
			return false;
		}
		
		User user = userDao.findById(email);
		if (user == null) {
			return false;
		}
		
		if (StringUtils.isBlank(sessionKey)) {
			return false;
		}
		
		Session session = null;
		if (user.getSessions() != null) {
			for (Session userSession : user.getSessions()) {
				if (userSession.getSessionKey().equals(sessionKey)) {
					session = userSession;
					break;
				}
			}
		}
		
		if (session != null) {
			if (session.isExpired()) {
				logout(email,  sessionKey);
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void markSessionUsed(String email, String sessionKey) {
		User user = userDao.findById(email);
		if (user == null)
			return;
		Session session = null;
		if (user.getSessions() != null) {
			for (Session userSession : user.getSessions()) {
				if (userSession.getSessionKey().equals(sessionKey)) {
					session = userSession;
					break;
				}
			}
		}
		if (session != null) {
			session.setTimesUsed(session.getTimesUsed() + 1);
			session.setLastTimeUsed(new Date());
			sessionDao.update(session);
		}
	}

	@Override
	public User findUser(String email) {
		User user = userDao.findById(email);
		User clonedUser = user.clone();		
		return user;
	}

	@Override
	public void changePermissions(String email, Role newRole)
			throws UserNotFoundException {
		User user = userDao.findById(email);
		if (user == null) {
			throw new UserNotFoundException();
		}
		
		user.setRole(newRole);
		userDao.update(user);
	}

	@Override
	public void resetPasswordRequest(String email) {
		User user = userDao.findById(email);
		if (user != null) {
			Transaction tx = datastores.get().getTransaction();
			try {
				PasswordResetRequest resetRequest = new PasswordResetRequest(user);
				passwordResetRequestDao.persist(resetRequest);
				
				String resetRequestKey = resetRequest.getKey();
				String resetUrl = "https://worktime-web.appspot.com/resetPassword/" + resetRequestKey;			
				String htmlBody = "<html><body><p>Dear WorkTime user,</p><p>You have requested a password reset for your online account.<br/>To reset your password follow this link: <a href=\"" + resetUrl + "\">" + resetUrl + "</a></p><p>If you cannot open the previous link then manually copy and paste the following url in your favorite browser:<br/>" + resetUrl + "</p><p>This password reset email is only valid for the next 24 hours. Afterwards this email will be unusable!</p>Kind Regards,<br/><br/>The WorkTime team!</body></html>";
				
				EmailUtil.sendEmail("WorkTime Password Reset", htmlBody, "text/html", Arrays.asList(new User[] {user}));
			} finally {
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
			}
		}
	}

	@Override
	public PasswordResetRequest getPasswordResetRequestKey(String passwordResetKey) 
			throws InvalidPasswordResetKeyException, PasswordResetKeyAlreadyUsedException, 
			PasswordResetKeyExpiredException {
		PasswordResetRequest resetRequest = passwordResetRequestDao.findById(passwordResetKey);
		
		if (resetRequest == null) {
			throw new InvalidPasswordResetKeyException();
		} else if (resetRequest.isUsed()) {
			throw new PasswordResetKeyAlreadyUsedException();
		} else if (resetRequest.isExpired()) {
			throw new PasswordResetKeyExpiredException();
		}
		
		return resetRequest;
	}

	@Override
	public void resetPassword(String passwordResetKey, String newPassword) 
			throws PasswordLenghtInvalidException, InvalidPasswordResetKeyException, 
			PasswordResetKeyAlreadyUsedException, PasswordResetKeyExpiredException {
		PasswordResetRequest resetRequest = getPasswordResetRequestKey(passwordResetKey);
		
		Password.validatePassword(newPassword);
		
		User user = userDao.findById(resetRequest.getEmail());
		user.setPasswordHash(Password.getSaltedHash(newPassword));
		userDao.update(user);
		
		resetRequest.setUsed(true);
		resetRequest.setUsedDate(new Date());
		passwordResetRequestDao.update(resetRequest);
	}

	@Override
	public Date getLogInTime(User user, String sessionKey) {
		for (Session session : user.getSessions()) {
			if (session.getSessionKey().equals(sessionKey)) {
				return session.getCreationDate();
			}
		}
		
		return null;
	}

	@Override
	public void logout(String email, String sessionKey) {
		User user = userDao.findById(email);
		
		if (user != null) {
			sessionDao.removeSession(user, sessionKey);
			user.removeSessionKey(sessionKey);
			userDao.update(user);
		}
	}

	@Override
	public List<User> findAll() {
		return userDao.findAll();
	}

	@Override
	public void update(User user) {
		User originalUser = userDao.findById(user.getEmail());
		originalUser.setEmail(user.getEmail().toLowerCase());
		originalUser.setFirstName(user.getFirstName());
		originalUser.setLastName(user.getLastName());
		originalUser.setProfileImageUrl(user.getProfileImageUrl());
		userDao.update(originalUser);
	}
}
