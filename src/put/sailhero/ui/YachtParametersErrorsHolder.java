package put.sailhero.ui;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class YachtParametersErrorsHolder extends EntityErrorsHolder {

	private LinkedList<String> mNameErrors = new LinkedList<String>();
	private LinkedList<String> mLengthErrors = new LinkedList<String>();
	private LinkedList<String> mWidthErrors = new LinkedList<String>();;
	private LinkedList<String> mCrewErrors = new LinkedList<String>();;

	public YachtParametersErrorsHolder(String json) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);
			JSONObject errorsObject = (JSONObject) obj.get("errors");
			if (errorsObject != null) {
				addJsonArrayErrorsToLinkedList((JSONArray) errorsObject.get("name"), mNameErrors);
				addJsonArrayErrorsToLinkedList((JSONArray) errorsObject.get("length"), mLengthErrors);
				addJsonArrayErrorsToLinkedList((JSONArray) errorsObject.get("width"), mWidthErrors);
				addJsonArrayErrorsToLinkedList((JSONArray) errorsObject.get("crew"), mCrewErrors);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public LinkedList<String> getNameErrors() {
		return mNameErrors;
	}

	public void setNameErrors(LinkedList<String> nameErrors) {
		mNameErrors = nameErrors;
	}

	public LinkedList<String> getLengthErrors() {
		return mLengthErrors;
	}

	public void setLengthErrors(LinkedList<String> lengthErrors) {
		mLengthErrors = lengthErrors;
	}

	public LinkedList<String> getWidthErrors() {
		return mWidthErrors;
	}

	public void setmWidthErrors(LinkedList<String> widthErrors) {
		mWidthErrors = widthErrors;
	}

	public LinkedList<String> getCrewErrors() {
		return mCrewErrors;
	}

	public void setCrewErrors(LinkedList<String> crewErrors) {
		mCrewErrors = crewErrors;
	}

}
