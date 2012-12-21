package eu.vranckaert.worktime.model;

import com.google.appengine.api.datastore.Text;
import com.vercer.engine.persist.annotation.Key;
import com.vercer.engine.persist.annotation.Type;

public class Service {
	@Key private String serviceKey;
	private String appName;
	private ServicePlatform platform;
	@Type(Text.class) private String contact;
	
	public String getServiceKey() {
		return serviceKey;
	}
	
	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public ServicePlatform getPlatform() {
		return platform;
	}
	
	public void setPlatform(ServicePlatform platform) {
		this.platform = platform;
	}
	
	public String getContact() {
		return contact;
	}
	
	public void setContact(String contact) {
		this.contact = contact;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serviceKey == null) ? 0 : serviceKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Service other = (Service) obj;
		if (serviceKey == null) {
			if (other.serviceKey != null)
				return false;
		} else if (!serviceKey.equals(other.serviceKey))
			return false;
		return true;
	}
}
