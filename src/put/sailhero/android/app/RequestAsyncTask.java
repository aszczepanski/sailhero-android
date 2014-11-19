package put.sailhero.android.app;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.TransportException;
import put.sailhero.android.exception.UnauthorizedException;
import put.sailhero.android.exception.UnprocessableEntityException;
import put.sailhero.android.util.Connection;
import put.sailhero.android.util.ProcessedResponse;
import put.sailhero.android.util.Request;
import put.sailhero.android.util.ResponseCreator;
import put.sailhero.android.util.SailHeroService;
import put.sailhero.android.util.SailHeroSettings;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class RequestAsyncTask extends AsyncTask<Void, Void, Void> {
	public final static String TAG = "sailhero";

	private SailHeroService mService = SailHeroService.getInstance();
	private SailHeroSettings mSettings = mService.getSettings();
	private Exception mException;
	private ProgressDialog mProgressDialog;
	private Request mRequest;
	private ResponseCreator mResponseCreator;
	private ProcessedResponse mProcessedResponse;

	private Context mContext;
	private AsyncRequestListener mAsyncRequestListener;

	public RequestAsyncTask(Request request, ResponseCreator responseCreator, Context context,
			AsyncRequestListener asyncRequestListener) {
		mRequest = request;
		mResponseCreator = responseCreator;
		mContext = context;
		mAsyncRequestListener = asyncRequestListener;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "", "Executing request...");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Connection connection = mService.getConnection();

		try {
			mProcessedResponse = connection.send(mRequest, mResponseCreator);
		} catch (Exception e) {
			mException = e;
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		mProgressDialog.dismiss();
		if (mException == null) {
			mAsyncRequestListener.onSuccess(mProcessedResponse, mRequest);
		} else if (mException instanceof UnprocessableEntityException) {
			Log.e(TAG, "unprocessable entity exception");
			mAsyncRequestListener.onUnprocessableEntityException(mException.getMessage());
		} else if (mException instanceof InvalidResourceOwnerException) {
			Log.e(TAG, mException.getMessage());
			mAsyncRequestListener.onInvalidResourceOwnerException();
		} else if (mException instanceof TransportException) {
			Log.e(TAG, "transport exception");
			mAsyncRequestListener.onTransportException();
		} else if (mException instanceof SystemException) {
			Log.e(TAG, "system exception");
			mAsyncRequestListener.onSystemException();
		} else if (mException instanceof UnauthorizedException) {
			Log.e(TAG, "unauthorized error");
			mAsyncRequestListener.onUnauthorizedException();
		} else {
			Log.e(TAG, "unknown error");
			assert (false);
		}
	}

	public static abstract class AsyncRequestListener {
		public abstract void onSuccess(ProcessedResponse processedResponse, Request request);

		public void onUnprocessableEntityException(String entityErrorsJson) {
			Log.e(TAG, "unexpected error");
		}

		public void onInvalidResourceOwnerException() {
			Log.e(TAG, "unexpected error");
		}

		public void onTransportException() {
			Log.e(TAG, "unexpected error");
		}

		public void onSystemException() {
			Log.e(TAG, "unexpected error");
		}

		public void onUnauthorizedException() {
			Log.e(TAG, "unexpected error");
		}
	}
}
