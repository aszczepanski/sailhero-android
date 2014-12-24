package put.sailhero.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import put.sailhero.R;
import put.sailhero.model.PoiModel;
import put.sailhero.model.Port;
import put.sailhero.provider.SailHeroContract;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SearchPoiActivity extends BaseActivity implements SailHeroListFragment.Listener,
		LoaderManager.LoaderCallbacks<Cursor> {

	private PoiAdapter mPoiAdapter;
	private Set<SailHeroListFragment> mPoiListFragments = new HashSet<SailHeroListFragment>();

	private String mCurFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_poi);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mPoiAdapter = new PoiAdapter(this);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.main_content, new SailHeroListFragment()).commit();
		}

		getLoaderManager().initLoader(PortQuery._TOKEN, null, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.search_port, menu);

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
						return true;
					}

					@Override
					public boolean onQueryTextChange(String s) {
						searchPoi(s);
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

	private void searchPoi(String newText) {
		String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

		if (TextUtils.equals(mCurFilter, newFilter)) {
			return;
		}
		mCurFilter = newFilter;
		getLoaderManager().restartLoader(PortQuery._TOKEN, null, this);
		return;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
		fragment.setListAdapter(mPoiAdapter);
	}

	@Override
	public void onFragmentAttached(SailHeroListFragment fragment) {
		mPoiListFragments.add(fragment);
	}

	@Override
	public void onFragmentDetached(SailHeroListFragment fragment) {
		mPoiListFragments.remove(fragment);
	}

	private interface PortQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = {
				SailHeroContract.Port.COLUMN_NAME_ID,
				SailHeroContract.Port.COLUMN_NAME_NAME,
				SailHeroContract.Port.COLUMN_NAME_CITY
		};

		int PORT_ID = 0;
		int PORT_NAME = 1;
		int PORT_CITY = 2;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case PortQuery._TOKEN: {
			Uri uri = SailHeroContract.Port.CONTENT_URI;
			if (!TextUtils.isEmpty(mCurFilter)) {
				String filter = '%' + mCurFilter + '%';
				return new CursorLoader(SearchPoiActivity.this, uri, PortQuery.PROJECTION,
						PortQuery.PROJECTION[PortQuery.PORT_NAME] + " LIKE ?", new String[] {
							filter
						}, null);
			} else {
				return new CursorLoader(SearchPoiActivity.this, uri, PortQuery.PROJECTION, null, null, null);
			}
		}
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		LinkedList<PoiModel> poiList = new LinkedList<PoiModel>();

		while (data.moveToNext()) {
			Port port = new Port();
			port.setId(data.getInt(PortQuery.PORT_ID));
			port.setName(data.getString(PortQuery.PORT_NAME));
			port.setCity(data.getString(PortQuery.PORT_CITY));

			poiList.add(port);
		}

		mPoiAdapter.updateItems(poiList);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
