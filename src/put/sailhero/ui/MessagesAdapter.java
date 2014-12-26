package put.sailhero.ui;

import java.util.ArrayList;
import java.util.LinkedList;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.Message;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MessagesAdapter implements ListAdapter {

	private static final int TAG_ID_FOR_VIEW_TYPE = R.id.message_viewtype_tagkey;
	private static final int VIEW_TYPE_NORMAL = 0;

	private static final String MY_VIEW_TAG = "SailHero_MY_VIEW_TAG";

	private Context mContext;

	private ArrayList<Message> mMessageList = new ArrayList<Message>();
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

		final Message message = mMessageList.get(position);

		LinearLayout mainBoxView = (LinearLayout) convertView.findViewById(R.id.main_box);
		TextView messageAuthorTextView = (TextView) convertView.findViewById(R.id.slot_message_author);
		TextView messageBodyTextView = (TextView) convertView.findViewById(R.id.slot_message_body);

		messageAuthorTextView.setText("author");
		messageAuthorTextView.setTextColor(mContext.getResources().getColor(R.color.body_text_1));
		messageAuthorTextView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);

		messageBodyTextView.setText(message.getBody());
		messageBodyTextView.setTextColor(mContext.getResources().getColor(R.color.body_text_2));

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
