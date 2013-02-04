package eu.vranckaert.worktime.guice;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceConfig extends GuiceServletContextListener {
	Logger log = Logger.getLogger(GuiceConfig.class.getName());
	
	protected Injector getInjector() {
		final Map<String, String> params = new HashMap<String, String>();
		/*
		 * The following line will scan our package for Jersey Resources
		 */
		params.put("com.sun.jersey.config.property.packages","eu.vranckaert.worktime.json.endpoint.impl");
		params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
		
		return Guice.createInjector(
				new GuiceModule(),
				new ServletModule() {
					@Override
					protected void configureServlets() {
						//bind(PlayersResource.class); //Works
						//bind(MainJerseyApplication.class); //Does not work 
						serve("/rest/*").with(GuiceContainer.class, params);
					}
				});
	}

}
