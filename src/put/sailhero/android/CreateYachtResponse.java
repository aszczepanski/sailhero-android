package put.sailhero.android;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class CreateYachtResponse implements ProcessedResponse {

	private Yacht yacht;
	
	public Yacht getYacht() {
		return yacht;
	}

	public void setYacht(Yacht yacht) {
		this.yacht = yacht;
	}

}
