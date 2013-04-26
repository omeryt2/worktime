package eu.vranckaert.worktime.util;
import com.google.sitebricks.headless.Request;


public class RequestUtil {
	public static final String getReferer(Request request) {
		if (request != null && request.headers() != null && request.headers().get("Referer") != null && request.headers().get("Referer").size() > 0) {
			String referPage = (String) request.headers().get("Referer").toArray()[0];
			return referPage;
		} else {
			return null;
		}
	}
}
