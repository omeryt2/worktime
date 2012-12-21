package eu.vranckaert.worktime.json.config;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
	private JAXBContext context;
	
	public JAXBContextResolver() throws Exception {
		this.context = new JSONJAXBContext(JSONConfiguration.natural().build());
	}

	@Override
	public JAXBContext getContext(Class<?> objectType) {
		return context;
	}

}
