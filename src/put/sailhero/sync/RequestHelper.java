package put.sailhero.sync;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import put.sailhero.Config;
import put.sailhero.exception.ForbiddenException;
import put.sailhero.exception.InvalidRegionException;
import put.sailhero.exception.InvalidResourceOwnerException;
import put.sailhero.exception.NoSpotException;
import put.sailhero.exception.NoYachtException;
import put.sailhero.exception.NotFoundException;
import put.sailhero.exception.NullUserException;
import put.sailhero.exception.SameUserException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.TransportException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.exception.UnprocessableEntityException;
import put.sailhero.exception.YachtAlreadyCreatedException;
import put.sailhero.model.User;
import put.sailhero.util.AccountUtils;
import put.sailhero.util.PrefUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.location.Location;
import android.net.Uri;

public abstract class RequestHelper {

	private HttpClient mHttpClient = new DefaultHttpClient();

	protected HttpUriRequest mHttpUriRequest;
	protected HttpResponse mHttpResponse;

	protected Context mContext;

	protected static final String CHARSET = "UTF-8";
	
	protected static final String PATH_API = "api";
	protected static final String PATH_VERSION = "v1";
	protected static final String PATH_I18N = "en";

	protected Uri BASE_URI = new Uri.Builder().scheme(Config.HOST_SCHEME)
			.encodedAuthority(Config.HOST_AUTHORITY)
			.build();

	protected Uri API_BASE_URI = BASE_URI.buildUpon()
			.appendPath(PATH_API)
			.appendPath(PATH_VERSION)
			.appendPath(PATH_I18N)
			.build();

	protected Uri OAUTH_BASE_URI = API_BASE_URI;

	public RequestHelper(Context context) {
		mContext = context;

		//		HttpParams httpParameters = new BasicHttpParams();
		//
		//		int timeoutConnection = 3000;
		//		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		//
		//		int timeoutSocket = 5000;
		//		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		//		mHttpClient = new DefaultHttpClient(httpParameters);
	}

	public boolean requiresAuthentication() {
		return true;
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

	protected void addHeaderPosition() {
		User user = PrefUtils.getUser(mContext);
		if (user != null && user.getSharePosition()) {
			Location lastKnownLocation = PrefUtils.getLastKnownLocation(mContext);
			if (lastKnownLocation != null) {
				mHttpUriRequest.addHeader("Latitude", String.valueOf(lastKnownLocation.getLatitude()));
				mHttpUriRequest.addHeader("longitude", String.valueOf(lastKnownLocation.getLongitude()));
			}
		}
	}

	protected void setHeaders() {
	}

	protected void setEntity() {
	}

	public void doRequest() throws TransportException, UnauthorizedException, SystemException,
			UnprocessableEntityException, InvalidResourceOwnerException, ForbiddenException, NotFoundException,
			InvalidRegionException, SameUserException, NullUserException, YachtAlreadyCreatedException,
			NoSpotException, NoYachtException {
		prepareHttpUriRequest();

		try {
			mHttpResponse = mHttpClient.execute(mHttpUriRequest);
			parseResponse();
		} catch (IOException e) {
			throw new TransportException(e.getMessage());
		}
	}

	protected abstract void parseResponse() throws UnauthorizedException, SystemException,
			UnprocessableEntityException, InvalidResourceOwnerException, ForbiddenException, NotFoundException,
			InvalidRegionException, SameUserException, NullUserException, YachtAlreadyCreatedException,
			NoSpotException, NoYachtException;

	public void storeData() throws SystemException {
	}
}
