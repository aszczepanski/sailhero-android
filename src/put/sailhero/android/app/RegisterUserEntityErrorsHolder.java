package put.sailhero.android.app;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RegisterUserEntityErrorsHolder extends EntityErrorsHolder {

	private LinkedList<String> mEmailErrors = new LinkedList<String>();
	private LinkedList<String> mPasswordErrors = new LinkedList<String>();
	private LinkedList<String> mPasswordConfirmationErrors = new LinkedList<String>();
	private LinkedList<String> mNameErrors = new LinkedList<String>();
	private LinkedList<String> mSurnameErrors = new LinkedList<String>();

	public RegisterUserEntityErrorsHolder(String json) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);
			JSONObject errorsObject = (JSONObject) obj.get("errors");
			if (errorsObject != null) {
				addJsonArrayErrorsToLinkedList((JSONArray) errorsObject.get("email"), mEmailErrors);
				addJsonArrayErrorsToLinkedList((JSONArray) errorsObject.get("password"), mPasswordErrors);
				addJsonArrayErrorsToLinkedList((JSONArray) errorsObject.get("password_confirmation"), mPasswordConfirmationErrors);
				addJsonArrayErrorsToLinkedList((JSONArray) errorsObject.get("name"), mNameErrors);
				addJsonArrayErrorsToLinkedList((JSONArray) errorsObject.get("surname"), mSurnameErrors);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public LinkedList<String> getEmailErrors() {
		return mEmailErrors;
	}

	public void setEmailErrors(LinkedList<String> emailErrors) {
		mEmailErrors = emailErrors;
	}

	public LinkedList<String> getPasswordErrors() {
		return mPasswordErrors;
	}

	public void setPasswordErrors(LinkedList<String> passwordErrors) {
		mPasswordErrors = passwordErrors;
	}

	public LinkedList<String> getPasswordConfirmationErrors() {
		return mPasswordConfirmationErrors;
	}

	public void setPasswordConfirmationErrors(LinkedList<String> passwordConfirmationErrors) {
		mPasswordConfirmationErrors = passwordConfirmationErrors;
	}

	public LinkedList<String> getNameErrors() {
		return mNameErrors;
	}

	public void setNameErrors(LinkedList<String> nameErrors) {
		mNameErrors = nameErrors;
	}

	public LinkedList<String> getSurnameErrors() {
		return mSurnameErrors;
	}

	public void setSurnameErrors(LinkedList<String> mSurnameErrors) {
		this.mSurnameErrors = mSurnameErrors;
	}
}
