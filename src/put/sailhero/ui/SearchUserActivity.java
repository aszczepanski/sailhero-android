package put.sailhero.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.User;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.sync.SearchUserRequestHelper;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SearchUserActivity extends BaseActivity implements UsersListFragment.Listener {

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

						LinkedList<UsersAdapter.UserContext> userContexts = new LinkedList<UsersAdapter.UserContext>();

						for (User user : searchUserRequest.getRetrievedUsers()) {
//							int status = SailHeroContract.Friendship.STATUS_STRANGER;
//
//							Log.i(Config.TAG, user.getEmail());
//							Cursor cursor = getContentResolver().query(SailHeroContract.Friendship.CONTENT_URI,
//									new String[] {
//										SailHeroContract.Friendship.COLUMN_NAME_STATUS
//									}, "id=?", new String[] {
//										user.getId().toString()
//									}, null);
//							if (cursor.getCount() > 0) {
//								status = cursor.getInt(0);
//							}
//							cursor.close();

							userContexts.add(new UsersAdapter.UserContext(user, null, null));
						}

						mUserAdapters[0].updateItems(userContexts);
					}
				});
		searchUserTask.execute();
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
}
