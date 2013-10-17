package eu.vranckaert.worktime.view.user;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.binding.FlashCache;
import com.google.sitebricks.client.transport.Text;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;
import com.google.sitebricks.rendering.Decorated;

import eu.vranckaert.worktime.security.service.UserService;
import eu.vranckaert.worktime.view.BaseService;
import eu.vranckaert.worktime.view.BaseView;
import eu.vranckaert.worktime.view.home.HomeView;

@At(LogoutService.PAGE_URL)
@Service
public class LogoutService extends BaseService {
	public static final String PAGE_URL = SECURED_PAGES_PREFIX + "logout";
	
	@Inject
	private FlashCache flashCache;
	
	@Inject
	private UserService userService;

	@Post
	public Reply<String> postReply(Request request) {
		Reply superReply = super.reply(request);
		if (superReply != null) {
			return superReply;
		}
		
		String sessionKey = flashCache.get(SESSION_KEY);
		String email = flashCache.get(EMAIL);
		
		userService.logout(email, sessionKey);
		
		if (StringUtils.isNotBlank(super.getRefererUrl()) && !super.getRefererUrl().contains(SECURED_PAGES_PREFIX)) {
			return Reply.with(super.getRefererUrl())
					.as(Text.class);
		} else {
			return Reply.with(HomeView.PAGE_URL)
					.as(Text.class);
		}
	}

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
	
}
