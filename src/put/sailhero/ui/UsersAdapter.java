package put.sailhero.ui;

import java.util.ArrayList;
import java.util.LinkedList;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.User;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.AcceptFriendshipRequestHelper;
import put.sailhero.sync.CancelFriendshipRequestHelper;
import put.sailhero.sync.CreateFriendshipRequestHelper;
import put.sailhero.sync.DeleteFriendshipRequestHelper;
import put.sailhero.sync.DenyFriendshipRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class UsersAdapter implements ListAdapter {

	private static final int TAG_ID_FOR_VIEW_TYPE = R.id.user_viewtype_tagkey;
	private static final int VIEW_TYPE_FRIEND = 0;
	private static final int VIEW_TYPE_PENDING = 1;
	private static final int VIEW_TYPE_SENT = 2;
	private static final int VIEW_TYPE_NORMAL = 3;

	private static final String MY_VIEW_TAG = "SailHero_MY_VIEW_TAG";

	private Context mContext;

	private ArrayList<UserContext> mUserContexts = new ArrayList<UserContext>();
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
		return mUserContexts.size();
	}

	@Override
	public Object getItem(int position) {
		return position >= 0 && position < mUserContexts.size() ? mUserContexts.get(position) : null;
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
		int layoutResId = R.layout.list_item_user;

		if (convertView == null || !MY_VIEW_TAG.equals(convertView.getTag())
				|| convertView.getTag(TAG_ID_FOR_VIEW_TYPE) == null
				|| !convertView.getTag(TAG_ID_FOR_VIEW_TYPE).equals(itemViewType)) {
			convertView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
					layoutResId, parent, false);
			convertView.setTag(TAG_ID_FOR_VIEW_TYPE, itemViewType);
		}

		if (position < 0 || position >= mUserContexts.size()) {
			Log.e(Config.TAG, "Invalid view position passed to UsersAdapter: " + position);
			return convertView;
		}

		convertView.setTag(MY_VIEW_TAG);

		final UserContext userContext = mUserContexts.get(position);

		LinearLayout mainBoxView = (LinearLayout) convertView.findViewById(R.id.main_box);
		LinearLayout bottomBoxView = (LinearLayout) convertView.findViewById(R.id.bottom_box);
		FrameLayout rightBoxView = (FrameLayout) convertView.findViewById(R.id.right_box);
		ImageView profileImageView = (ImageView) convertView.findViewById(R.id.profile_image);
		TextView userTitleTextView = (TextView) convertView.findViewById(R.id.slot_user_title);
		TextView userSubtitleTextView = (TextView) convertView.findViewById(R.id.slot_user_subtitle);

		TextView inviteButton = (TextView) convertView.findViewById(R.id.invite_text_view);
		TextView acceptButton = (TextView) convertView.findViewById(R.id.accept_text_view);
		TextView denyButton = (TextView) convertView.findViewById(R.id.deny_text_view);
		TextView cancelButton = (TextView) convertView.findViewById(R.id.cancel_text_view);
		TextView removeButton = (TextView) convertView.findViewById(R.id.remove_text_view);

		// boxView.setBackgroundResource(R.drawable.user_item_background_normal);
		// boxView.setForeground(null);

		final User user = userContext.getUser();
		final Integer friendshipId = userContext.getFriendshipId();
		Integer status = userContext.getResponseStatus();

		userTitleTextView.setText(user.getName() + " " + user.getSurname());
		userTitleTextView.setTextColor(mContext.getResources().getColor(R.color.body_text_1));
		userTitleTextView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);

		userSubtitleTextView.setText(user.getEmail());
		userSubtitleTextView.setTextColor(mContext.getResources().getColor(R.color.body_text_2));

		if (user.getAvatarUrl() != null) {
			Glide.with(mContext).load(user.getAvatarUrl()).asBitmap().into(profileImageView);
		}

		if (status == SailHeroContract.Friendship.STATUS_STRANGER) {
			bottomBoxView.setVisibility(View.GONE);
			inviteButton.setVisibility(View.VISIBLE);
			inviteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO:
					RequestHelperAsyncTask createFriendshipTask = new RequestHelperAsyncTask(mContext,
							new CreateFriendshipRequestHelper(mContext, user.getId()),
							new RequestHelperAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(RequestHelper requestHelper) {
									Toast.makeText(mContext, "User invited.", Toast.LENGTH_SHORT).show();
									notifyObservers();
								}
							});
					createFriendshipTask.execute();
				}
			});
		} else if (status == SailHeroContract.Friendship.STATUS_ACCEPTED) {
			bottomBoxView.setVisibility(View.GONE);
			removeButton.setVisibility(View.VISIBLE);
			removeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					RequestHelperAsyncTask deleteFriendshipTask = new RequestHelperAsyncTask(mContext,
							new DeleteFriendshipRequestHelper(mContext, friendshipId),
							new RequestHelperAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(RequestHelper requestHelper) {
									Toast.makeText(mContext, "Friendship deleted.", Toast.LENGTH_SHORT).show();
									notifyObservers();
								}
							});
					deleteFriendshipTask.execute();
				}
			});
		} else if (status == SailHeroContract.Friendship.STATUS_PENDING) {
			acceptButton.setVisibility(View.VISIBLE);
			acceptButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO:
					RequestHelperAsyncTask acceptFriendshipTask = new RequestHelperAsyncTask(mContext,
							new AcceptFriendshipRequestHelper(mContext, friendshipId),
							new RequestHelperAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(RequestHelper requestHelper) {
									Toast.makeText(mContext, "Friendship accepted.", Toast.LENGTH_SHORT).show();
									notifyObservers();
								}
							});
					acceptFriendshipTask.execute();
				}
			});

			denyButton.setVisibility(View.VISIBLE);
			denyButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					RequestHelperAsyncTask denyFriendshipTask = new RequestHelperAsyncTask(mContext,
							new DenyFriendshipRequestHelper(mContext, friendshipId),
							new RequestHelperAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(RequestHelper requestHelper) {
									Toast.makeText(mContext, "Friendship denied.", Toast.LENGTH_SHORT).show();
									notifyObservers();
								}
							});
					denyFriendshipTask.execute();
				}
			});
		} else if (status == SailHeroContract.Friendship.STATUS_SENT) {
			bottomBoxView.setVisibility(View.GONE);
			cancelButton.setVisibility(View.VISIBLE);
			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					RequestHelperAsyncTask cancelFriendshipTask = new RequestHelperAsyncTask(mContext,
							new CancelFriendshipRequestHelper(mContext, friendshipId),
							new RequestHelperAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(RequestHelper requestHelper) {
									Toast.makeText(mContext, "Friendship canceled.", Toast.LENGTH_SHORT).show();
									notifyObservers();
								}
							});
					cancelFriendshipTask.execute();
				}
			});
		} else {
			Log.e(Config.TAG, "Invalid item type in UsersAdapter.");
		}

		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		if (position < 0 || position >= mUserContexts.size()) {
			Log.e(Config.TAG, "Invalid position passed to UsersAdapter (" + position + ")");
			return VIEW_TYPE_NORMAL;
		}

		UserContext userContext = mUserContexts.get(position);
		switch (userContext.getResponseStatus()) {
		case SailHeroContract.Friendship.STATUS_STRANGER:
			return VIEW_TYPE_NORMAL;
		case SailHeroContract.Friendship.STATUS_SENT:
			return VIEW_TYPE_SENT;
		case SailHeroContract.Friendship.STATUS_PENDING:
			return VIEW_TYPE_PENDING;
		case SailHeroContract.Friendship.STATUS_ACCEPTED:
			return VIEW_TYPE_FRIEND;
		default:
			return VIEW_TYPE_NORMAL;
		}
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

	public void updateItems(LinkedList<UserContext> userContexts) {
		Log.e(Config.TAG, "size2: " + userContexts.size());
		mUserContexts.clear();
		if (userContexts != null) {
			for (UserContext userContext : userContexts) {
				Log.d(Config.TAG, "Adding user item: " + userContext.getUser().getEmail());
				mUserContexts.add(userContext);
			}
		}
		notifyObservers();
	}

	@Override
	public boolean isEmpty() {
		return mUserContexts.isEmpty();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	public static class UserContext {
		private User mUser;
		private Integer mResponseStatus;
		private Integer mFriendshipId;

		public UserContext(User user, Integer responseStatus, Integer friendshipId) {
			mUser = user;
			mResponseStatus = responseStatus;
			mFriendshipId = friendshipId;
		}

		public User getUser() {
			return mUser;
		}

		public void setUser(User user) {
			mUser = user;
		}

		public Integer getResponseStatus() {
			return mResponseStatus;
		}

		public void setResponseStatus(Integer status) {
			mResponseStatus = status;
		}

		public Integer getFriendshipId() {
			return mFriendshipId;
		}

		public void setFriendshipId(Integer id) {
			mFriendshipId = id;
		}
	}

}
