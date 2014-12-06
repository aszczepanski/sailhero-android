package put.sailhero.ui;

import java.util.ArrayList;
import java.util.LinkedList;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.User;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class UsersAdapter implements ListAdapter {

	private static final int VIEW_TYPE_FRIEND = 0;
	private static final int VIEW_TYPE_PENDING = 1;
	private static final int VIEW_TYPE_SENT = 2;
	private static final int VIEW_TYPE_NORMAL = 3;

	private Context mContext;

	private ArrayList<User> mUsers = new ArrayList<User>();
	private ArrayList<DataSetObserver> mObservers = new ArrayList<DataSetObserver>();

	public UsersAdapter(Context context) {
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
		return mUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return position >= 0 && position < mUsers.size() ? mUsers.get(position) : null;
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
		// TODO Auto-generated method stub
		int itemViewType = getItemViewType(position);
		int layoutResId = R.layout.user_item;

		if (convertView == null) {
			convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
					layoutResId, parent, false);
		}

		if (position < 0 || position >= mUsers.size()) {
			Log.e(Config.TAG, "Invalid view position passed to MyScheduleAdapter: " + position);
			return convertView;
		}

		User user = mUsers.get(position);

		TextView tv = (TextView) convertView.findViewById(R.id.tv);
		tv.setText(user.getEmail());

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return VIEW_TYPE_NORMAL;
	}

	@Override
	public int getViewTypeCount() {
		return 4;
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

	public void updateItems(LinkedList<User> users) {
		mUsers.clear();
		if (users != null) {
			for (User user : users) {
				Log.d(Config.TAG, "Adding user item: " + user.getEmail());
				mUsers.add(user); // TODO: clone
			}
		}
		notifyObservers();
	}

	@Override
	public boolean isEmpty() {
		return mUsers.isEmpty();
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
