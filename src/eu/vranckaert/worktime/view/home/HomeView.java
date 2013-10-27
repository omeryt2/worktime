package eu.vranckaert.worktime.view.home;

import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.rendering.Decorated;

import eu.vranckaert.worktime.view.BaseView;

@At(HomeView.PAGE_URL)
@Show("/WEB-INF/pages/home.jsp")
//@Decorated
public class HomeView extends BaseView {
	public static final String PAGE_URL = "/";
	
	private String test = "TEST";

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
		return null;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}
}
