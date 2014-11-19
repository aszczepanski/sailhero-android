package put.sailhero.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AuthenticatorService extends Service {

	private static final String TAG = "sailhero";

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Creating authenticator service");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Destroying authenticator service");
	}

	@Override
	public IBinder onBind(Intent intent) {
		AccountAuthenticator authenticator = new AccountAuthenticator(this);
		return authenticator.getIBinder();
	}
}
