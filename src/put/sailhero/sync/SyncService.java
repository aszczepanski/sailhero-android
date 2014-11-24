package put.sailhero.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {

	public static final String TAG = "sailhero";

	private static final Object sSyncAdapterLock = new Object();
	private static SyncAdapter sSyncAdapter = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Service created");
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Service destroyed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}
}
