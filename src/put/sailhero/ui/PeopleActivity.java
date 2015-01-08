package put.sailhero.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.User;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.ui.widget.SlidingTabLayout;
import put.sailhero.util.SyncUtils;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PeopleActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
		SailHeroListFragment.Listener {

	private static final String ARG_FRIENDSHIP_STATUS = "put.sailhero.ARG_FRIENDSHIP_STATUS";

	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;

	private UsersAdapter[] mUserAdapters = new UsersAdapter[3];
	private Set<SailHeroListFragment> mUserListFragments = new HashSet<SailHeroListFragment>();

	int FRIENDSHIP_ACCEPTED_FRAGMENT = 0;
	int FRIENDSHIP_SENT_FRAGMENT = 1;
	int FRIENDSHIP_PENDING_FRAGMENT = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_people);

		overridePendingTransition(0, 0);

		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setAdapter(new PeoplePagerAdapter(getFragmentManager()));

		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
		mSlidingTabLayout.setDistributeEvenly(true);
		mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tab_selected_strip));

		mSlidingTabLayout.setViewPager(mViewPager);

		for (int i = 0; i < mUserAdapters.length; i++) {
			mUserAdapters[i] = new UsersAdapter(this);
		}

		getLoaderManager().initLoader(FriendshipQuery._TOKEN, null, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_PEOPLE;
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d(TAG, "PeopleActivity::onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void requestDataRefresh() {
		super.requestDataRefresh();

		SyncUtils.syncFriendships(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.people, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search_user:
			startActivity(new Intent(this, SearchUserActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class PeoplePagerAdapter extends FragmentPagerAdapter {

		public PeoplePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "friends list";
			case 1:
				return "pending";
			case 2:
				return "sent";
			default:
				return null;
			}
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(Config.TAG, "Creating fragment #" + position);
			SailHeroListFragment fragment = new SailHeroListFragment();
			Bundle args = new Bundle();
			switch (position) {
			case 0:
				args.putInt(ARG_FRIENDSHIP_STATUS, FRIENDSHIP_ACCEPTED_FRAGMENT);
				break;
			case 1:
				args.putInt(ARG_FRIENDSHIP_STATUS, FRIENDSHIP_PENDING_FRAGMENT);
				break;
			case 2:
				args.putInt(ARG_FRIENDSHIP_STATUS, FRIENDSHIP_SENT_FRAGMENT);
				break;
			}

			fragment.setArguments(args);
			return fragment;
		}

	}

	private interface FriendshipQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = {
				SailHeroContract.Friendship.COLUMN_NAME_ID,
				SailHeroContract.Friendship.COLUMN_NAME_STATUS,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_ID,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_EMAIL,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_NAME,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_SURNAME,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_AVATAR_URL
		};

		int FRIENDSHIP_ID = 0;
		int FRIENDSHIP_STATUS = 1;
		int FRIENDSHIP_FRIEND_ID = 2;
		int FRIENDSHIP_FRIEND_EMAIL = 3;
		int FRIENDSHIP_FRIEND_NAME = 4;
		int FRIENDSHIP_FRIEND_SURNAME = 5;
		int FRIENDSHIP_FRIEND_AVATAR_URL = 6;
	}

	private void onFriendshipLoaderComplete(Cursor cursor) {
		LinkedList<UsersAdapter.UserContext> friendshipsAccepted = new LinkedList<UsersAdapter.UserContext>();
		LinkedList<UsersAdapter.UserContext> friendshipsPending = new LinkedList<UsersAdapter.UserContext>();
		LinkedList<UsersAdapter.UserContext> friendshipsSent = new LinkedList<UsersAdapter.UserContext>();

		Log.e(TAG, "!!1!!!!!!!!!!!!!!!!!!!!!!!!!");
		Log.e(TAG, "size: " + cursor.getCount());

		while (cursor.moveToNext()) {
			User friend = new User();
			friend.setId(cursor.getInt(FriendshipQuery.FRIENDSHIP_FRIEND_ID));
			friend.setEmail(cursor.getString(FriendshipQuery.FRIENDSHIP_FRIEND_EMAIL));
			friend.setName(cursor.getString(FriendshipQuery.FRIENDSHIP_FRIEND_NAME));
			friend.setSurname(cursor.getString(FriendshipQuery.FRIENDSHIP_FRIEND_SURNAME));
			friend.setAvatarUrl(cursor.getString(FriendshipQuery.FRIENDSHIP_FRIEND_AVATAR_URL));

			int status = cursor.getInt(FriendshipQuery.FRIENDSHIP_STATUS);
			int friendshipId = cursor.getInt(FriendshipQuery.FRIENDSHIP_ID);
			if (status == SailHeroContract.Friendship.STATUS_ACCEPTED) {
				friendshipsAccepted.add(new UsersAdapter.UserContext(friend, status, friendshipId));
			} else if (status == SailHeroContract.Friendship.STATUS_PENDING) {
				friendshipsPending.add(new UsersAdapter.UserContext(friend, status, friendshipId));
			} else if (status == SailHeroContract.Friendship.STATUS_SENT) {
				friendshipsSent.add(new UsersAdapter.UserContext(friend, status, friendshipId));
			}

			Log.e(TAG, "friendship: " + friend.getEmail() + " " + status);
		}

		mUserAdapters[FRIENDSHIP_ACCEPTED_FRAGMENT].updateItems(friendshipsAccepted);
		mUserAdapters[FRIENDSHIP_PENDING_FRAGMENT].updateItems(friendshipsPending);
		mUserAdapters[FRIENDSHIP_SENT_FRAGMENT].updateItems(friendshipsSent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case FriendshipQuery._TOKEN: {
			Uri uri = SailHeroContract.Friendship.CONTENT_URI;
			return new CursorLoader(PeopleActivity.this, uri, FriendshipQuery.PROJECTION, null, null, null);
		}
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case FriendshipQuery._TOKEN:
			onFriendshipLoaderComplete(data);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.w(TAG, "PeopleActivity::onLoaderReset");
	}

	@Override
	public void onFragmentViewCreated(ListFragment fragment) {
		Log.e(TAG, "here!!!");
		int status = fragment.getArguments().getInt(ARG_FRIENDSHIP_STATUS, FRIENDSHIP_ACCEPTED_FRAGMENT);
		fragment.setListAdapter(mUserAdapters[status]);
	}

	@Override
	public void onFragmentAttached(SailHeroListFragment fragment) {
		mUserListFragments.add(fragment);
	}

	@Override
	public void onFragmentDetached(SailHeroListFragment fragment) {
		mUserListFragments.remove(fragment);
	}
}
