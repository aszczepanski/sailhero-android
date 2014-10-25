package put.sailhero.android.app;

import java.util.ArrayList;

import put.sailhero.android.util.SailHeroService;
import put.sailhero.android.util.SailHeroSettings;
import put.sailhero.android.util.model.Region;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class SelectRegionDialogFragment extends DialogFragment {

	private SailHeroService mService;
	private SailHeroSettings mSettings;

	private Context mContext;

	public SelectRegionDialogFragment(Context context) {
		mContext = context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		mService = SailHeroService.getInstance();
		mSettings = mService.getSettings();

		Region region = new Region();
		region.setId(1);
		region.setFullName("full name");
		region.setCodeName("code name");
		ArrayList<Region> regions = new ArrayList<Region>();
		regions.add(region);
		regions.add(region);

		CharSequence[] regionNames = new CharSequence[mSettings.getRegionsList().size()];
		for (int i = 0; i < regionNames.length; i++) {
			regionNames[i] = mSettings.getRegionsList().get(i).getFullName();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle("Select a region");
		builder.setItems(regionNames, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Region selectedRegion = mSettings.getRegionsList().get(which);
				Log.i("sailhero", selectedRegion.getFullName());
			}
		});

		return builder.create();
	}

	public interface SelectRegionDialogListener {

	}
}
