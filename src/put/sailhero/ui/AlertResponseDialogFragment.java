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
				RequestHelperAsyncTask confirmAlertTask = new RequestHelperAsyncTask(mContext,
						new ConfirmAlertRequestHelper(mContext, mAlert.getId()),
						new RequestHelperAsyncTask.AsyncRequestListener() {
							@Override
							public void onSuccess(RequestHelper requestHelper) {
								// TODO
							}
						});
				confirmAlertTask.execute();
			}
		});

		builder.setNegativeButton(getString(R.string.alert_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RequestHelperAsyncTask cancelAlertTask = new RequestHelperAsyncTask(mContext,
						new CancelAlertRequestHelper(mContext, mAlert.getId()),
						new RequestHelperAsyncTask.AsyncRequestListener() {
							@Override
							public void onSuccess(RequestHelper requestHelper) {
								// TODO
							}
						});
				cancelAlertTask.execute();
			}
		});

		return builder.create();
	}

}
