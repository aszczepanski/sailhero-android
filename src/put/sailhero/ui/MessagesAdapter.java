package put.sailhero.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.Message;
import put.sailhero.model.User;
import put.sailhero.util.PrefUtils;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MessagesAdapter implements ListAdapter {

	private static final int TAG_ID_FOR_VIEW_TYPE = R.id.message_viewtype_tagkey;
	private static final int VIEW_TYPE_NORMAL = 0;

	private static final String MY_VIEW_TAG = "SailHero_MY_VIEW_TAG";

	private Context mContext;

	private ArrayList<Message> mMessageList = new ArrayList<Message>();
	private SparseArray<User> mSenders = new SparseArray<User>();
	private ArrayList<DataSetObserver> mObservers = new ArrayList<DataSetObserver>();

	public MessagesAdapter(Context context) {
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
		return mMessageList.size();
	}

	@Override
	public Object getItem(int position) {
		return position >= 0 && position < mMessageList.size() ? mMessageList.get(position) : null;
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
		int layoutResId = R.layout.list_item_message;

		Log.d(Config.TAG, "getView()");

		if (convertView == null || !MY_VIEW_TAG.equals(convertView.getTag())
				|| convertView.getTag(TAG_ID_FOR_VIEW_TYPE) == null
				|| !convertView.getTag(TAG_ID_FOR_VIEW_TYPE).equals(itemViewType)) {
			Log.d(Config.TAG, "inflating new view");
			convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
					layoutResId, parent, false);
			convertView.setTag(TAG_ID_FOR_VIEW_TYPE, itemViewType);
		}

		if (position < 0 || position >= mMessageList.size()) {
			Log.e(Config.TAG, "Invalid view position passed to MessagesAdapter: " + position);
			return convertView;
		}

		convertView.setTag(MY_VIEW_TAG);

		final User currentUser = PrefUtils.getUser(mContext);
		final Message message = mMessageList.get(position);

		LinearLayout mainBoxView = (LinearLayout) convertView.findViewById(R.id.main_box);
		ImageView profileLeftImageView = (ImageView) convertView.findViewById(R.id.profile_image_left);
		profileLeftImageView.setVisibility(View.GONE);
		ImageView profileRightImageView = (ImageView) convertView.findViewById(R.id.profile_image_right);
		profileRightImageView.setVisibility(View.GONE);
		ImageView profileImageView = null;
		if (message.getUserId().equals(currentUser.getId())) {
			profileImageView = profileRightImageView;
		} else {
			profileImageView = profileLeftImageView;
		}
		profileImageView.setVisibility(View.VISIBLE);

		TextView messageBodyTextView = (TextView) convertView.findViewById(R.id.slot_message_body);
		TextView messageDateTextView = (TextView) convertView.findViewById(R.id.slot_message_date);

		User sender = mSenders.get(message.getUserId());
		if (sender != null) {
			Glide.with(mContext)
					.load(sender.getAvatarUrl())
					.asBitmap()
					.error(R.drawable.person_image_empty)
					.into(profileImageView);
		}

		messageBodyTextView.setText(message.getBody());

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.UK);
		messageDateTextView.setText(formatter.format(message.getCreatedAt()));

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		if (position < 0 || position >= mMessageList.size()) {
			Log.e(Config.TAG, "Invalid position passed to MessagesAdapter (" + position + ")");
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

	public void updateItems(LinkedList<Message> messageList) {
		Log.e(Config.TAG, "size3: " + messageList.size());
		mMessageList.clear();
		if (messageList != null) {
			for (Message message : messageList) {
				Log.d(Config.TAG, "Adding message item: " + message.getBody());
				mMessageList.add(message);
			}
		}
		notifyObservers();
	}

	public void updateSenders(LinkedList<User> sendersList) {
		for (User user : sendersList) {
			mSenders.put(user.getId(), user);
		}
		notifyObservers();
	}

	@Override
	public boolean isEmpty() {
		return mMessageList.isEmpty();
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
