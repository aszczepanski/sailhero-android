package put.sailhero.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import put.sailhero.R;
import put.sailhero.exception.InvalidRegionException;
import put.sailhero.model.Message;
import put.sailhero.model.User;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.sync.RetrieveMessagesRequestHelper;
import put.sailhero.sync.RetrieveUserRequestHelper;
import put.sailhero.sync.SendMessageRequestHelper;
import put.sailhero.util.SyncUtils;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MessageActivity extends BaseActivity implements SailHeroListFragment.Listener,
		SailHeroListFragment.ScrollListener {

	public final static String MESSAGE_SYNC_MESSAGES = "message";

	private final static int FETCH_LIMIT = 15;

	private MessagesAdapter mMessagesAdapter;

	private LinkedList<Message> mMessagesList = new LinkedList<Message>();
	private LinkedList<User> mRetrievedSenders = new LinkedList<User>();
	private Set<Integer> mSyncedSendersIds = new HashSet<Integer>();

	private Integer mNextMessageId;
	private Integer mPreviousMessageId;

	private EditText mNewMessageEditText;
	private Button mSendButton;

	private ProgressBar mLoadMoreProgressBar;

	private BroadcastReceiver mGcmReceiver;

	boolean isFetchingOldActive = false;
	boolean isFetchingNewestActive = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new SailHeroListFragment()).commit();
		}

		overridePendingTransition(0, 0);

		mMessagesAdapter = new MessagesAdapter(this);

		mNewMessageEditText = (EditText) findViewById(R.id.new_message_edit_text);

		mLoadMoreProgressBar = (ProgressBar) findViewById(R.id.load_more_progress_bar);

		Button mRefreshOldButton = (Button) findViewById(R.id.refresh_old_button);
		mRefreshOldButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fetchOld();
			}
		});

		Button refreshTillNowButton = (Button) findViewById(R.id.refresh_till_now_button);
		refreshTillNowButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fetchNewestMessages();
			}
		});

		mSendButton = (Button) findViewById(R.id.send_button);
		mSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String msgBody = mNewMessageEditText.getText().toString().trim();
				if (msgBody.isEmpty()) {
					Toast.makeText(MessageActivity.this, "Cannot send empty message.", Toast.LENGTH_SHORT).show();
					return;
				}
				SendMessageRequestHelper requestHelper = new SendMessageRequestHelper(MessageActivity.this, msgBody);
				RequestHelperAsyncTask task = new RequestHelperAsyncTask(MessageActivity.this, "Sending message",
						msgBody, requestHelper, new RequestHelperAsyncTask.AsyncRequestListener() {
							@Override
							public void onSuccess(RequestHelper requestHelper) {
								Toast.makeText(MessageActivity.this, "Message sent.", Toast.LENGTH_SHORT).show();
								Log.d(TAG, "Message sent");

								mNewMessageEditText.setText("");
								hideSoftKeyboardFrom(mNewMessageEditText);

								// TODO:  fetch messages
							}

							@Override
							public void onInvalidRegionException(RequestHelper requestHelper) {
								Toast.makeText(MessageActivity.this, "Choose a region first.", Toast.LENGTH_SHORT)
										.show();
							}
						});
				task.execute();
			}
		});

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.google.android.c2dm.intent.RECEIVE");

		mGcmReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "gcm programatically received");
				Toast.makeText(MessageActivity.this, "gcm received", Toast.LENGTH_SHORT).show();

				Bundle extras = intent.getExtras();
				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(MessageActivity.this);

				String messageType = gcm.getMessageType(intent);

				Log.e(TAG, messageType);

				if (!extras.isEmpty()) {
					if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
						Log.e(TAG, "Send error: " + extras.toString());
					} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
						Log.e(TAG, "Deleted messages on server: " + extras.toString());
					} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
						String message = extras.getString("message");
						Log.i(TAG, "message: " + message);

						if (message != null && message.equals(MESSAGE_SYNC_MESSAGES)) {
							fetchNewestMessages();
						}
					}
				}
			}
		};

		registerReceiver(mGcmReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mGcmReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();

		fetchNewestMessages();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void fetchOld() {
		synchronized (this) {
			if (isFetchingOldActive) {
				return;
			} else {
				isFetchingOldActive = true;
			}
		}
		Integer limit = FETCH_LIMIT;
		Integer since = mPreviousMessageId;
		if (since == null) {
			if (!mMessagesList.isEmpty()) {
				since = mMessagesList.getLast().getId();
			}
		}
		synchronized (this) {
			if (since == null && isFetchingNewestActive) {
				isFetchingOldActive = false;
				return;
			}
		}
		String order = RetrieveMessagesRequestHelper.ORDER_DESC;

		Log.e(TAG, limit + ", " + since + ", " + order);

		RetrieveMessagesRequestHelper requestHelper = new RetrieveMessagesRequestHelper(MessageActivity.this, limit,
				since, order);
		LoadMoreMessagesAsyncTask task = new LoadMoreMessagesAsyncTask(MessageActivity.this, requestHelper);
		task.execute();
	}

	private void fetchNewestMessages() {
		synchronized (this) {
			if (isFetchingNewestActive) {
				return;
			} else {
				isFetchingNewestActive = true;
			}
		}
		Integer limit = FETCH_LIMIT;
		Integer since = mNextMessageId;
		if (since == null) {
			if (!mMessagesList.isEmpty()) {
				since = mMessagesList.getFirst().getId();
			}
		}
		synchronized (this) {
			if (since == null && isFetchingOldActive) {
				isFetchingNewestActive = false;
				return;
			}
		}
		String order = RetrieveMessagesRequestHelper.ORDER_ASC;

		Log.e(TAG, limit + ", " + since + ", " + order);

		if (since == null) {
			synchronized (MessageActivity.this) {
				isFetchingNewestActive = false;
			}
			fetchOld();
		} else {
			RetrieveMessagesRequestHelper requestHelper = new RetrieveMessagesRequestHelper(MessageActivity.this,
					limit, since, order);
			LoadMoreMessagesAsyncTask task = new LoadMoreMessagesAsyncTask(MessageActivity.this, requestHelper);
			task.execute();
		}
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_MESSAGES;
	}

	@Override
	public void onFragmentViewCreated(ListFragment fragment) {
		fragment.setListAdapter(mMessagesAdapter);
	}

	@Override
	public void onFragmentAttached(SailHeroListFragment fragment) {
	}

	@Override
	public void onFragmentDetached(SailHeroListFragment fragment) {
	}

	private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	private int mFirstVisibleItem;
	private int mVisibleItemCount;
	private int mTotalItemCount;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mScrollState = scrollState;

		if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& mFirstVisibleItem + mVisibleItemCount == mTotalItemCount) {
			fetchOld();
		}
		if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE && mFirstVisibleItem == 0) {
			fetchNewestMessages();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		mTotalItemCount = totalItemCount;
	}

	private void hideSoftKeyboardFrom(final EditText et) {
		if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		}
	}

	private class LoadMoreMessagesAsyncTask extends AsyncTask<Void, Void, Void> {

		final private Context mContext;
		final private RetrieveMessagesRequestHelper mRequestHelper;

		private Exception mException;

		public LoadMoreMessagesAsyncTask(final Context context, RetrieveMessagesRequestHelper requestHelper) {
			mContext = context;
			mRequestHelper = requestHelper;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadMoreProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				SyncUtils.doAuthenticatedRequest(mContext, mRequestHelper);
			} catch (Exception e) {
				mException = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			mLoadMoreProgressBar.setVisibility(View.GONE);

			if (mException == null) {
				if (mRequestHelper.getSentOrder() == RetrieveMessagesRequestHelper.ORDER_DESC) {
					for (Message msg : mRequestHelper.getRetrievedMessages()) {
						if (mMessagesList.isEmpty() || !msg.getId().equals(mMessagesList.getLast().getId())) {
							mMessagesList.addLast(msg);
							if (!mSyncedSendersIds.contains(msg.getUserId())) {
								mSyncedSendersIds.add(msg.getUserId());
								retrieveSender(msg.getUserId());
							}
						}
					}

					mMessagesAdapter.updateItems(mMessagesList);

					mPreviousMessageId = mRequestHelper.getRetrievedNextMessageId();

					synchronized (MessageActivity.this) {
						isFetchingOldActive = false;
					}
				} else {
					for (Message msg : mRequestHelper.getRetrievedMessages()) {
						if (mMessagesList.isEmpty() || !msg.getId().equals(mMessagesList.getFirst().getId())) {
							mMessagesList.addFirst(msg);
							if (!mSyncedSendersIds.contains(msg.getUserId())) {
								mSyncedSendersIds.add(msg.getUserId());
								retrieveSender(msg.getUserId());
							}
						}
					}

					mMessagesAdapter.updateItems(mMessagesList);

					mNextMessageId = mRequestHelper.getRetrievedNextMessageId();

					synchronized (MessageActivity.this) {
						isFetchingNewestActive = false;
					}

					if (mNextMessageId != null) {
						fetchNewestMessages();
					}
				}
			} else if (mException instanceof InvalidRegionException) {
				Toast.makeText(mContext, "Choose a region first.", Toast.LENGTH_SHORT).show();
			} else {
			}
		}
	}

	private void retrieveSender(Integer senderId) {
		Log.e(TAG, "retrieving sender: " + senderId);
		RetrieveSender retrieveSenderTask = new RetrieveSender(MessageActivity.this, senderId);
		retrieveSenderTask.execute();
	}

	private class RetrieveSender extends AsyncTask<Void, Void, Void> {

		private Context mContext;
		private Integer mSenderId;
		private RetrieveUserRequestHelper mRequestHelper;
		private Exception mException;

		public RetrieveSender(Context context, Integer senderId) {
			mContext = context;
			mSenderId = senderId;
			mRequestHelper = new RetrieveUserRequestHelper(mContext, mSenderId);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				SyncUtils.doAuthenticatedRequest(mContext, mRequestHelper);
			} catch (Exception e) {
				mException = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mException == null && mRetrievedSenders != null) {
				mRetrievedSenders.add(mRequestHelper.getRetrievedUser());
				Log.e(TAG, mRequestHelper.getRetrievedUser().getEmail());
				mMessagesAdapter.updateSenders(mRetrievedSenders);
			}
		}

	}

}
