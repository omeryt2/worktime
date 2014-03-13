package eu.vranckaert.worktime.guice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.sitebricks.SitebricksModule;
import com.google.sitebricks.binding.FlashCache;
import com.google.sitebricks.binding.HttpSessionFlashCache;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import eu.vranckaert.worktime.cron.reporting.ReportNewUsersServlet;
import eu.vranckaert.worktime.view.BaseView;

public class GuiceConfig extends GuiceServletContextListener {
	private Logger log = Logger.getLogger(GuiceConfig.class.getName());
	
	protected Injector getInjector() {
		final Map<String, String> params = new HashMap<String, String>();
		/*
		 * The following line will scan our package for Jersey Resources
		 */
		params.put("com.sun.jersey.config.property.packages","eu.vranckaert.worktime.json.endpoint.impl");
		params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
		
		return Guice.createInjector(
				new GuiceModule(),
				new SitebricksModule() {
					@Override
					protected void configureSitebricks() {
						super.scan(BaseView.class.getPackage());
//						localize(UIMessages.class).usingDefault();
//						try {
//							mapLocale(SupportedLocales.DUTCH);
//						} catch (IOException e) {
//							throw new RuntimeException(e);
//						}
					}
					
//					private void mapLocale(Locale locale) throws IOException {
//						String propName = "uimessages_" + locale.getLanguage();
//						if (!StringUtils.isBlank(locale.getCountry())) {
//							propName += "_" + locale.getCountry().toUpperCase();
//						}
//						propName += ".properties";
//						
//						propName = "/eu/vranckaert/worktime/ui/i18n/" + propName;
//						
//						Properties properties = new Properties();
//						InputStream inputStream = this.getClass().getResourceAsStream(propName);
//						
//						if (inputStream == null) {
//							throw new FileNotFoundException("Could not find the translated file " + propName);
//						}
//						properties.load(inputStream);
//						inputStream.close();
//						
//						localize(UIMessages.class).using(locale, properties);
//					}
				},
				new ServletModule() {
					@Override
					protected void configureServlets() {
						serve("/rest/*").with(GuiceContainer.class, params);
						serve("/cron/reportNewUsers").with(ReportNewUsersServlet.class);
						bind(ReportNewUsersServlet.class).in(Scopes.SINGLETON);
						bind(FlashCache.class).to(HttpSessionFlashCache.class);
					}
				});
	}
	
	private static class SupportedLocales {
		private static final Locale DUTCH = new Locale("nl");
	}
}
