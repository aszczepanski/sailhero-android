package put.sailhero.ui;

import java.util.ArrayList;
import java.util.LinkedList;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.PoiModel;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class PoiAdapter implements ListAdapter {

	private static final int TAG_ID_FOR_VIEW_TYPE = R.id.port_viewtype_tagkey;
	private static final int VIEW_TYPE_NORMAL = 0;

	private static final String MY_VIEW_TAG = "SailHero_MY_VIEW_TAG";

	private Context mContext;

	private ArrayList<PoiModel> mPoiList = new ArrayList<PoiModel>();
	private ArrayList<DataSetObserver> mObservers = new ArrayList<DataSetObserver>();

	public PoiAdapter(Context context) {
		mContext = context;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		if (!mObservers.contains(observer)) {
			mObservers.add(observer);
		}
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (mObservers.contains(observer)) {
			mObservers.remove(observer);
		}
	}

	@Override
	public int getCount() {
		return mPoiList.size();
	}

	@Override
	public Object getItem(int position) {
		return position >= 0 && position < mPoiList.size() ? mPoiList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int itemViewType = getItemViewType(position);
		int layoutResId = R.layout.list_item_poi;

		Log.d(Config.TAG, "getView()");

		if (convertView == null || !MY_VIEW_TAG.equals(convertView.getTag())
				|| convertView.getTag(TAG_ID_FOR_VIEW_TYPE) == null
				|| !convertView.getTag(TAG_ID_FOR_VIEW_TYPE).equals(itemViewType)) {
			Log.d(Config.TAG, "inflating new view");
			convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
					layoutResId, parent, false);
			convertView.setTag(TAG_ID_FOR_VIEW_TYPE, itemViewType);
		}

		if (position < 0 || position >= mPoiList.size()) {
			Log.e(Config.TAG, "Invalid view position passed to PoiAdapter: " + position);
			return convertView;
		}

		convertView.setTag(MY_VIEW_TAG);

		final PoiModel poi = mPoiList.get(position);

		LinearLayout mainBoxView = (LinearLayout) convertView.findViewById(R.id.main_box);
		FrameLayout rightBoxView = (FrameLayout) convertView.findViewById(R.id.right_box);
		TextView poiNameTextView = (TextView) convertView.findViewById(R.id.slot_poi_name);
		TextView poiCityTextView = (TextView) convertView.findViewById(R.id.slot_poi_city);

		TextView detailsButton = (TextView) convertView.findViewById(R.id.details_text_view);

		poiNameTextView.setText(poi.getName());
		poiNameTextView.setTextColor(mContext.getResources().getColor(R.color.body_text_1));
		poiNameTextView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);

		poiCityTextView.setText(poi.getCity());
		poiCityTextView.setTextColor(mContext.getResources().getColor(R.color.body_text_2));

		detailsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent detailsIntent = poi.getDetailsIntent(mContext);
				mContext.startActivity(detailsIntent);
			}
		});

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		if (position < 0 || position >= mPoiList.size()) {
			Log.e(Config.TAG, "Invalid position passed to PortsAdapter (" + position + ")");
			return VIEW_TYPE_NORMAL;
		}

		return VIEW_TYPE_NORMAL;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	public void clear() {
		updateItems(null);
	}

	private void notifyObservers() {
		for (DataSetObserver observer : mObservers) {
			observer.onChanged();
		}
	}

	public void forceUpdate() {
		notifyObservers();
	}

	public void updateItems(LinkedList<PoiModel> poiList) {
		Log.e(Config.TAG, "size3: " + poiList.size());
		mPoiList.clear();
		if (poiList != null) {
			for (PoiModel poi : poiList) {
				Log.d(Config.TAG, "Adding poi item: " + poi.getName());
				mPoiList.add(poi);
			}
		}
		notifyObservers();
	}

	@Override
	public boolean isEmpty() {
		return mPoiList.isEmpty();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

}
