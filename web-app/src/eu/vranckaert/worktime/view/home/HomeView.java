package eu.vranckaert.worktime.view.home;

import com.google.sitebricks.At;
import com.google.sitebricks.Show;

import eu.vranckaert.worktime.view.BaseView;

@At(HomeView.PAGE_URL)
@Show("WEB-INF/pages/home.html")
public class HomeView extends BaseView {
	public static final String PAGE_URL = "/";
}
