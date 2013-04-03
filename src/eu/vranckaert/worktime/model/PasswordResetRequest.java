package eu.vranckaert.worktime.model;

import java.util.Calendar;
import java.util.Date;

import com.google.code.twig.annotation.Entity;
import com.google.code.twig.annotation.Id;

import eu.vranckaert.worktime.security.utils.KeyGenerator;

@Entity(kind="passwordResetRequest")
public class PasswordResetRequest {
	@Id private String key;
	private Date requestDate;
	private String email;
	private boolean used;
	private Date usedDate;
	
	public PasswordResetRequest() {}
	
	public PasswordResetRequest(User user) {
		key = KeyGenerator.getNewKey();
		requestDate = new Date();
		this.email = user.getEmail();
		this.used = false;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Date getRequestDate() {
		return requestDate;
	}
	
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean isUsed() {
		return used;
	}
	
	public void setUsed(boolean used) {
		this.used = used;
	}

	public Date getUsedDate() {
		return usedDate;
	}

	public void setUsedDate(Date usedDate) {
		this.usedDate = usedDate;
	}
	
	public boolean isExpired() {
		if (used) {
			return true;
		}
		
		Calendar expireDate = Calendar.getInstance();
		expireDate.setTime(this.requestDate);
		expireDate.add(Calendar.HOUR_OF_DAY, 24);
		
		Date now = new Date();
		
		if (now.after(expireDate.getTime())) {
			return true;
		}
		
		return false;
	}
}
