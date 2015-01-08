package put.sailhero.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.PoiModel;
import put.sailhero.model.Port;
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

public class PoiActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
		SailHeroListFragment.Listener {

	private static final String ARG_POI_NAME = "put.sailhero.ARG_POI_NAME";

	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;

	private PoiAdapter mPortsAdapter;
	private Set<SailHeroListFragment> mPoiFragments = new HashSet<SailHeroListFragment>();

	private static final int PORTS_FRAGMENT = 0;
	private static final int INVALID_FRAGMENT = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_people);

		overridePendingTransition(0, 0);

		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setAdapter(new PoiPagerAdapter(getFragmentManager()));

		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
		mSlidingTabLayout.setDistributeEvenly(true);
		mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.tab_selected_strip));

		mSlidingTabLayout.setViewPager(mViewPager);

		mPortsAdapter = new PoiAdapter(this);

		getLoaderManager().initLoader(PortQuery._TOKEN, null, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_POI;
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
	protected void requestDataRefresh() {
		super.requestDataRefresh();

		SyncUtils.syncPorts(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.poi, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search_poi:
			startActivity(new Intent(this, SearchPoiActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class PoiPagerAdapter extends FragmentPagerAdapter {

		public PoiPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "ports";
			default:
				return null;
			}
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(Config.TAG, "Creating fragment #" + position);
			Fragment fragment;
			Bundle args = new Bundle();

			switch (position) {
			case 0:
				fragment = new SailHeroListFragment();
				args.putInt(ARG_POI_NAME, PORTS_FRAGMENT);
				break;
			default:
				return null;
			}

			fragment.setArguments(args);
			return fragment;
		}

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

	private void onPortLoaderComplete(Cursor cursor) {
		LinkedList<PoiModel> poiList = new LinkedList<PoiModel>();

		Log.e(TAG, "size: " + cursor.getCount());

		while (cursor.moveToNext()) {
			Port port = new Port();
			port.setId(cursor.getInt(PortQuery.PORT_ID));
			port.setName(cursor.getString(PortQuery.PORT_NAME));
			port.setCity(cursor.getString(PortQuery.PORT_CITY));

			poiList.add(port);

			Log.e(TAG, "port: " + port.getName());
		}

		mPortsAdapter.updateItems(poiList);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case PortQuery._TOKEN: {
			Uri uri = SailHeroContract.Port.CONTENT_URI;
			return new CursorLoader(PoiActivity.this, uri, PortQuery.PROJECTION, null, null, null);
		}
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case PortQuery._TOKEN:
			onPortLoaderComplete(data);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	@Override
	public void onFragmentViewCreated(ListFragment fragment) {
		int status = fragment.getArguments().getInt(ARG_POI_NAME, INVALID_FRAGMENT);
		switch (status) {
		case PORTS_FRAGMENT:
			fragment.setListAdapter(mPortsAdapter);
			break;
		}
	}

	@Override
	public void onFragmentAttached(SailHeroListFragment fragment) {
		mPoiFragments.add(fragment);
	}

	@Override
	public void onFragmentDetached(SailHeroListFragment fragment) {
		mPoiFragments.remove(fragment);
	}
}
