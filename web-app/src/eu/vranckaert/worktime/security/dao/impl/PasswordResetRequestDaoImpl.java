package eu.vranckaert.worktime.security.dao.impl;

import eu.vranckaert.worktime.dao.impl.BaseDaoImpl;
import eu.vranckaert.worktime.model.PasswordResetRequest;
import eu.vranckaert.worktime.security.dao.PasswordResetRequestDao;

public class PasswordResetRequestDaoImpl extends
		BaseDaoImpl<PasswordResetRequest> implements PasswordResetRequestDao {

	public PasswordResetRequestDaoImpl() {
		super(PasswordResetRequest.class);
	}
}
