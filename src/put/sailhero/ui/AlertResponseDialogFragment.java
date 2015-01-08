package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.model.Alert;
import put.sailhero.sync.CancelAlertRequestHelper;
import put.sailhero.sync.ConfirmAlertRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.util.StringUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class AlertResponseDialogFragment extends DialogFragment {

	private final Context mContext;
	private final Alert mAlert;

	public AlertResponseDialogFragment(Context context, Alert alert) {
		super();

		mContext = context;
		mAlert = alert;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		builder.setTitle(StringUtils.getStringForAlertType(mContext, mAlert.getAlertType()));
		builder.setMessage("Do you confirm this alert?");

		builder.setPositiveButton(getString(R.string.alert_accept), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RequestHelperAsyncTask confirmAlertTask = new RequestHelperAsyncTask(mContext, "Alert confirmation",
						"Confirming alert...", new ConfirmAlertRequestHelper(mContext, mAlert.getId()),
						serverResponseListener);
				confirmAlertTask.execute();
			}
		});

		builder.setNegativeButton(getString(R.string.alert_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RequestHelperAsyncTask cancelAlertTask = new RequestHelperAsyncTask(mContext, "Alert confirmation",
						"Denying alert...", new CancelAlertRequestHelper(mContext, mAlert.getId()),
						serverResponseListener);
				cancelAlertTask.execute();
			}
		});

		return builder.create();
	}

	private final RequestHelperAsyncTask.AsyncRequestListener serverResponseListener = new RequestHelperAsyncTask.AsyncRequestListener() {
		@Override
		public void onSuccess(RequestHelper requestHelper) {
			Toast.makeText(mContext, "Response saved.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onForbiddenException(RequestHelper requestHelper) {
			Toast.makeText(mContext, "You cannot respond to your alert.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onInvalidRegionException(RequestHelper requestHelper) {
			Toast.makeText(mContext, "Choose a region first.", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onNotFoundException(RequestHelper requestHelper) {
			Toast.makeText(mContext, "Alert not found.", Toast.LENGTH_SHORT).show();
		}
	};

}
