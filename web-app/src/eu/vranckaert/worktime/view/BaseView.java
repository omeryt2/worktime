package eu.vranckaert.worktime.view;

import javax.inject.Inject;

import eu.vranckaert.worktime.view.i18n.UIMessages;

public abstract class BaseView {
	@Inject
    private UIMessages messages;

	private String infoMessage;
	private String warningMessage;
	private String errorMessage;

	public UIMessages getMessages() {
		return messages;
	}

	public String getInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
