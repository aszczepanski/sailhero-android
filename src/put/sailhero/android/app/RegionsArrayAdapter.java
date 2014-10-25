package put.sailhero.android.app;

import java.util.ArrayList;

import put.sailhero.android.R;
import put.sailhero.android.util.model.Region;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RegionsArrayAdapter extends ArrayAdapter<Region> {
	public RegionsArrayAdapter(Context context, ArrayList<Region> regions) {
		super(context, R.layout.dialog_fragment_select_region_row, regions);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Region region = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.dialog_fragment_select_region_row, parent, false);
		}

		TextView tv = (TextView) convertView
				.findViewById(R.id.DialogFragmentSelectRegionRowTextView);
		tv.setText(region.getFullName());

		return convertView;
	}
}
