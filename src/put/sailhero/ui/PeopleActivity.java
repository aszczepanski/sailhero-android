package put.sailhero.ui;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.SearchUserActivity;
import put.sailhero.ui.widget.SlidingTabLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PeopleActivity extends BaseActivity {

	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;

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

	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_PEOPLE;
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
				return "invitations";
			case 2:
				return "search";
			default:
				return null;
			}
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(Config.TAG, "Creating fragment #" + position);
			switch (position) {
			case 0:
				return new FriendsListFragment();
			case 1:
				return new FriendsInvitationsListFragment();
			case 2:
				return new SearchFriendsFragment();
			default:
				return null;
			}
		}

	}
}
