package put.sailhero.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.Friendship;
import put.sailhero.model.User;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.sync.SearchUserRequestHelper;
import put.sailhero.util.SyncUtils;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SearchUserActivity extends BaseActivity implements UsersListFragment.Listener {

	private LinkedList<User> mFoundUsers;

	private UsersAdapter[] mUserAdapters = new UsersAdapter[1];
	private Set<UsersListFragment> mUserListFragments = new HashSet<UsersListFragment>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_user);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		for (int i = 0; i < mUserAdapters.length; i++) {
			mUserAdapters[i] = new UsersAdapter(this);
		}

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.main_content, new UsersListFragment()).commit();
		}

		getContentResolver().registerContentObserver(SailHeroContract.Friendship.CONTENT_URI, true,
				mFriendshipsObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		getContentResolver().unregisterContentObserver(mFriendshipsObserver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.search_user, menu);

		final MenuItem searchItem = menu.findItem(R.id.action_search);
		if (searchItem != null) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			final SearchView searchView = (SearchView) searchItem.getActionView();
			if (searchView == null) {
				Log.w(TAG, "Could not set up search view, view is null.");
			} else {
				searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
				searchView.setIconified(false);
				searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

					@Override
					public boolean onQueryTextSubmit(String s) {
						searchView.clearFocus();
						Toast.makeText(SearchUserActivity.this, "Submit: " + s, Toast.LENGTH_SHORT).show();

						searchUser(s);

						return true;
					}

					@Override
					public boolean onQueryTextChange(String s) {
						return true;
					}
				});
				searchView.setOnCloseListener(new SearchView.OnCloseListener() {
					@Override
					public boolean onClose() {
						finish();
						return false;
					}
				});
			}
		}

		return true;
	}

	private void searchUser(String s) {
		RequestHelperAsyncTask searchUserTask = new RequestHelperAsyncTask(SearchUserActivity.this,
				new SearchUserRequestHelper(SearchUserActivity.this, s),
				new RequestHelperAsyncTask.AsyncRequestListener() {
					@Override
					public void onSuccess(RequestHelper requestHelper) {
						Log.e(Config.TAG, "onSuccess");
						SearchUserRequestHelper searchUserRequest = (SearchUserRequestHelper) requestHelper;

						mFoundUsers = searchUserRequest.getRetrievedUsers();

						reloadFriendships();
						SyncUtils.syncFriendships(SearchUserActivity.this);
					}
				});
		searchUserTask.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	private final ContentObserver mFriendshipsObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			reloadFriendships();
		}
	};

	private void reloadFriendships() {
		if (mFoundUsers == null) {
			return;
		}

		LinkedList<UsersAdapter.UserContext> userContexts = new LinkedList<UsersAdapter.UserContext>();

		for (User user : mFoundUsers) {
			Friendship friendship = new Friendship();

			Cursor cursor = getContentResolver().query(SailHeroContract.Friendship.CONTENT_URI,
					FriendshipQuery.PROJECTION, SailHeroContract.Friendship.COLUMN_NAME_FRIEND_ID + "=?", new String[] {
						user.getId().toString()
					}, null);

			friendship.setFriend(user);

			if (cursor.moveToFirst()) {
				friendship.setId(cursor.getInt(FriendshipQuery.FRIENDSHIP_ID));
				friendship.setStatus(cursor.getInt(FriendshipQuery.FRIENDSHIP_STATUS));
			} else {
				friendship.setStatus(SailHeroContract.Friendship.STATUS_STRANGER);
			}

			cursor.close();

			userContexts.add(new UsersAdapter.UserContext(friendship.getFriend(), friendship.getStatus(),
					friendship.getId()));
		}

		mUserAdapters[0].updateItems(userContexts);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_search) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFragmentViewCreated(ListFragment fragment) {
		fragment.setListAdapter(mUserAdapters[0]);
	}

	@Override
	public void onFragmentAttached(UsersListFragment fragment) {
		mUserListFragments.add(fragment);
	}

	@Override
	public void onFragmentDetached(UsersListFragment fragment) {
		mUserListFragments.remove(fragment);
	}

	private interface FriendshipQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = {
				SailHeroContract.Friendship.COLUMN_NAME_ID,
				SailHeroContract.Friendship.COLUMN_NAME_STATUS,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_ID,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_EMAIL,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_NAME,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_SURNAME
		};

		int FRIENDSHIP_ID = 0;
		int FRIENDSHIP_STATUS = 1;
		int FRIENDSHIP_FRIEND_ID = 2;
		int FRIENDSHIP_FRIEND_EMAIL = 3;
		int FRIENDSHIP_FRIEND_NAME = 4;
		int FRIENDSHIP_FRIEND_SURNAME = 5;
	}
}
