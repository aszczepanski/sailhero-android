package put.sailhero.sync;

import put.sailhero.exception.ForbiddenException;
import put.sailhero.exception.InvalidRegionException;
import put.sailhero.exception.InvalidResourceOwnerException;
import put.sailhero.exception.NotFoundException;
import put.sailhero.exception.NullUserException;
import put.sailhero.exception.SameUserException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.TransportException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.exception.UnprocessableEntityException;
import put.sailhero.exception.YachtAlreadyCreatedException;
import put.sailhero.util.SyncUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class RequestHelperAsyncTask extends AsyncTask<Void, Void, Void> {

	public static final String TAG = "sailhero";

	private Context mContext;

	private RequestHelper mRequestHelper;
	private Exception mException;

	private ProgressDialog mProgressDialog;

	private AsyncRequestListener mAsyncRequestListener;

	public RequestHelperAsyncTask(Context context, RequestHelper requestHelper,
			AsyncRequestListener asyncRequestListener) {
		mContext = context;
		mRequestHelper = requestHelper;
		mAsyncRequestListener = asyncRequestListener;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "", "Executing request...");
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			SyncUtils.doAuthenticatedRequest(mContext, mRequestHelper);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		mProgressDialog.dismiss();
		if (mException == null) {
			mAsyncRequestListener.onSuccess(mRequestHelper);
		} else if (mException instanceof ForbiddenException) {
			Log.e(TAG, "forbidden exception");
			mAsyncRequestListener.onForbiddenException(mRequestHelper);
		} else if (mException instanceof InvalidRegionException) {
			Log.e(TAG, "invalid region exception");
			mAsyncRequestListener.onInvalidRegionException(mRequestHelper);
		} else if (mException instanceof InvalidResourceOwnerException) {
			Log.e(TAG, mException.getMessage());
			mAsyncRequestListener.onInvalidResourceOwnerException(mRequestHelper);
		} else if (mException instanceof NotFoundException) {
			Log.e(TAG, "not found exception");
			mAsyncRequestListener.onNotFoundException(mRequestHelper);
		} else if (mException instanceof NullUserException) {
			Log.e(TAG, "null user exception");
			mAsyncRequestListener.onNullUserException(mRequestHelper);
		} else if (mException instanceof UnprocessableEntityException) {
			Log.e(TAG, "unprocessable entity exception");
			mAsyncRequestListener.onUnprocessableEntityException(mRequestHelper, mException.getMessage());
		} else if (mException instanceof SameUserException) {
			Log.e(TAG, "same user exception");
			mAsyncRequestListener.onSameUserException(mRequestHelper);
		} else if (mException instanceof SystemException) {
			Log.e(TAG, "system exception");
			mAsyncRequestListener.onSystemException(mRequestHelper);
		} else if (mException instanceof TransportException) {
			Log.e(TAG, "transport exception");
			mAsyncRequestListener.onTransportException(mRequestHelper);
		} else if (mException instanceof UnauthorizedException) {
			Log.e(TAG, "unauthorized error");
			mAsyncRequestListener.onUnauthorizedException(mRequestHelper);
		} else if (mException instanceof YachtAlreadyCreatedException) {
			Log.e(TAG, "yacht already created exception");
			mAsyncRequestListener.onYachtAlreadyCreatedException(mRequestHelper);
		} else {
			Log.e(TAG, "unknown error");
			assert false;
		}
	}

	public static abstract class AsyncRequestListener {
		public abstract void onSuccess(RequestHelper requestHelper);

		public void onForbiddenException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}

		public void onInvalidRegionException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}

		public void onInvalidResourceOwnerException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}

		public void onNotFoundException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}

		public void onNullUserException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}

		public void onSameUserException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}

		public void onSystemException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}

		public void onTransportException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}

		public void onUnauthorizedException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}

		public void onUnprocessableEntityException(RequestHelper requestHelper, String entityErrorsJson) {
			Log.e(TAG, "unexpected error");
		}

		public void onYachtAlreadyCreatedException(RequestHelper requestHelper) {
			Log.e(TAG, "unexpected error");
		}
	}
}
