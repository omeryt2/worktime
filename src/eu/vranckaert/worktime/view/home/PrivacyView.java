package eu.vranckaert.worktime.view.home;

import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.rendering.Decorated;

import eu.vranckaert.worktime.view.BaseView;

@At(PrivacyView.PAGE_URL)
@Show("/WEB-INF/pages/privacy.jsp")
//@Decorated
public class PrivacyView extends BaseView {
public static final String PAGE_URL = "/privacy";

	private String website = "http://worktime-web.appspot.com";

	@Override
	protected void fetchUrlParameters(String... urlParameters) {}

	@Override
	public String getPageUrl() {
		return PAGE_URL;
	}

	@Override
	public String getFullPageUrl() {
		return PAGE_URL;
	}

	@Override
	public String getPageTitle() {
		return getMessage("privacyPolicy.title");
	}

	public String getWebsite() {
		return website;
	}
}
