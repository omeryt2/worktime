package eu.vranckaert.worktime.view;

import com.google.sitebricks.client.transport.Text;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;

public abstract class BaseService extends BaseView {
	public Reply<? extends Object> reply(Request request) {
		String superResult = super.post(request);
		if (shouldRedirect(superResult)) {
			return Reply.with(superResult)
					.as(Text.class);
		}
		
		return null;
	}
}
