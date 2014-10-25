package put.sailhero.android.util;

import put.sailhero.android.util.model.Alert;

public class CreateAlertResponse implements ProcessedResponse {

	private Alert alert;

	public Alert getAlert() {
		return alert;
	}

	public void setAlert(Alert alert) {
		this.alert = alert;
	}
}
