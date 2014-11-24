package put.sailhero.sync;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import put.sailhero.account.AccountUtils;
import put.sailhero.exception.InvalidResourceOwnerException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.TransportException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.exception.UnprocessableEntityException;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;

public abstract class RequestHelper {

	private HttpClient mHttpClient = new DefaultHttpClient();

	protected HttpUriRequest mHttpUriRequest;
	protected HttpResponse mHttpResponse;

	protected Context mContext;

	public RequestHelper(Context context) {
		mContext = context;
	}

	private void prepareHttpUriRequest() {
		createMethodClient();
		setHeaders();
		setEntity();
	}

	protected abstract void createMethodClient();

	protected void addHeaderAuthorization() {
		Account account = AccountUtils.getActiveAccount(mContext);
		AccountManager accountManager = AccountManager.get(mContext);

		String accessToken = "";
		try {
			accessToken = accountManager.blockingGetAuthToken(account, AccountUtils.ACCESS_TOKEN_TYPE, true);
		} catch (OperationCanceledException | AuthenticatorException | IOException e) {
			e.printStackTrace();
		}

		mHttpUriRequest.addHeader("Authorization", "Bearer " + accessToken);
	}

	protected void addHeaderContentJson() {
		mHttpUriRequest.addHeader("Content-Type", "application/json");
	}

	protected void setHeaders() {
	}

	protected void setEntity() {
	}

	public void doRequest() throws TransportException, UnauthorizedException, SystemException,
			UnprocessableEntityException, InvalidResourceOwnerException {
		prepareHttpUriRequest();

		try {
			mHttpResponse = mHttpClient.execute(mHttpUriRequest);
			parseResponse();
		} catch (IOException e) {
			throw new TransportException(e.getMessage());
		}
	}

	protected abstract void parseResponse() throws UnauthorizedException, SystemException,
			UnprocessableEntityException, InvalidResourceOwnerException;

	public void storeData() throws SystemException {
	}
}
