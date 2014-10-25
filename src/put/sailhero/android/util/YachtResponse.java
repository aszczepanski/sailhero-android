package put.sailhero.android.util;

import put.sailhero.android.util.model.Yacht;


public class YachtResponse implements ProcessedResponse {

	private Yacht yacht;
	
	public Yacht getYacht() {
		return yacht;
	}

	public void setYacht(Yacht yacht) {
		this.yacht = yacht;
	}

}
