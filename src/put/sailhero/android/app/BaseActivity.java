package put.sailhero.android.app;

import java.util.ArrayList;

import put.sailhero.android.AccountUtils;
import put.sailhero.android.R;
import android.accounts.Account;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class BaseActivity extends ActionBarActivity {

	public final static String TAG = "sailhero";

	protected static final int NAVDRAWER_ITEM_MAIN = 0;
	protected static final int NAVDRAWER_ITEM_ALERT = 1;
	protected static final int NAVDRAWER_ITEM_MAP = 2;
	protected static final int NAVDRAWER_ITEM_WEATHER = 3;
	protected static final int NAVDRAWER_ITEM_SETTINGS = 4;
	protected static final int NAVDRAWER_ITEM_INVALID = -1;
	protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;

	private static final int[] NAVDRAWER_TITLE_RES_ID = new int[] {
			R.string.navdrawer_item_main,
			R.string.navdrawer_item_alert,
			R.string.navdrawer_item_map,
			R.string.navdrawer_item_weather,
			R.string.navdrawer_item_settings
	};

	private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_drawer_settings
	};

	private static final int NAVDRAWER_LAUNCH_DELAY = 250;

	private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
	private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

	private Handler mHandler;
	private Runnable mDeferredOnDrawerClosedRunnable;

	private ViewGroup mDrawerItemsListContainer;
	private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();
	private View[] mNavDrawerItemViews = null;

	private Toolbar mActionBarToolbar;
	private DrawerLayout mDrawerLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new Handler();

		if (savedInstanceState != null) {
			// TODO
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		if (account == null) {
			Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(loginIntent);
			finish();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		int itemId = getSelfNavDrawerItem();
		if (itemId != NAVDRAWER_ITEM_INVALID) {
			setSelectedNavDrawerItem(itemId);
		}

		View mainContent = findViewById(R.id.main_content);
		if (mainContent != null) {
			mainContent.setAlpha(0);
			mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
		} else {
			Log.w(TAG, "No view with ID main_content to fade in.");
		}
	}

	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_INVALID;
	}

	public void setupNavDrawer() {
		int selfItem = getSelfNavDrawerItem();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (mDrawerLayout == null) {
			return;
		}
		mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.theme_primary_dark));

		ScrollView navDrawerScrollView = (ScrollView) findViewById(R.id.navdrawer);
		if (selfItem == NAVDRAWER_ITEM_INVALID) {
			// do not show a nav drawer
			if (navDrawerScrollView != null) {
				((ViewGroup) navDrawerScrollView.getParent()).removeView(navDrawerScrollView);
			}
			mDrawerLayout = null;
			return;
		}

		if (navDrawerScrollView != null) {
			// TODO
		}

		if (mActionBarToolbar != null) {
			mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer);
			mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mDrawerLayout.openDrawer(Gravity.START);
				}
			});
		}

		mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerClosed(View drawerView) {
				// run deferred action, if we have one
				if (mDeferredOnDrawerClosedRunnable != null) {
					mDeferredOnDrawerClosedRunnable.run();
					mDeferredOnDrawerClosedRunnable = null;
				}
			}

			@Override
			public void onDrawerOpened(View drawerView) {
			}

			@Override
			public void onDrawerStateChanged(int newState) {
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
			}
		});

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

		populateNavDrawer();

		// When the user runs the app for the first time, we want to land them with the
		// navigation drawer open. But just the first time.
		//        if (!PrefUtils.isWelcomeDone(this)) {
		//            // first run of the app starts with the nav drawer open
		//            PrefUtils.markWelcomeDone(this);
		//            mDrawerLayout.openDrawer(Gravity.START);
		//        }
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		setupActionBarToolbar();
	}

	protected boolean isNavDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START);
	}

	protected void closeNavDrawer() {
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(Gravity.START);
		}
	}

	private void populateNavDrawer() {
		mNavDrawerItems.clear();

		mNavDrawerItems.add(NAVDRAWER_ITEM_MAIN);

		mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);

		mNavDrawerItems.add(NAVDRAWER_ITEM_ALERT);
		mNavDrawerItems.add(NAVDRAWER_ITEM_MAP);
		mNavDrawerItems.add(NAVDRAWER_ITEM_WEATHER);

		mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);

		mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);

		createNavDrawerItems();
	}

	@Override
	public void onBackPressed() {
		if (isNavDrawerOpen()) {
			closeNavDrawer();
		} else {
			super.onBackPressed();
		}
	}

	private void createNavDrawerItems() {
		mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
		if (mDrawerItemsListContainer == null) {
			return;
		}

		mNavDrawerItemViews = new View[mNavDrawerItems.size()];
		mDrawerItemsListContainer.removeAllViews();
		int i = 0;
		for (int itemId : mNavDrawerItems) {
			mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
			mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
			++i;
		}
	}

	private void setSelectedNavDrawerItem(int itemId) {
		if (mNavDrawerItemViews != null) {
			for (int i = 0; i < mNavDrawerItemViews.length; i++) {
				if (i < mNavDrawerItems.size()) {
					int thisItemId = mNavDrawerItems.get(i);
					formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
				}
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupNavDrawer();

		// TODO: setup other things
	}

	private void goToNavDrawerItem(int item) {
		Intent intent = null;
		switch (item) {
		case NAVDRAWER_ITEM_MAIN:
			intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			break;
		case NAVDRAWER_ITEM_ALERT:
			intent = new Intent(this, AlertActivity.class);
			break;
		case NAVDRAWER_ITEM_MAP:
			intent = new Intent(this, TestMapActivity.class);
			break;
		case NAVDRAWER_ITEM_SETTINGS:
			intent = new Intent(this, PreferenceActivity.class);
			break;
		default:
			return;
		}

//		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		if (getSelfNavDrawerItem() != NAVDRAWER_ITEM_MAIN && !isSpecialItem(item)) {
			finish();
		}
		startActivity(intent);
	}

	private void onNavDrawerItemClicked(final int itemId) {
		if (itemId == getSelfNavDrawerItem()) {
			mDrawerLayout.closeDrawer(Gravity.START);
			return;
		}

		// launch the target Activity after a short delay, to allow the close animation to play
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				goToNavDrawerItem(itemId);
			}
		}, NAVDRAWER_LAUNCH_DELAY);

		// fade out the main content
		// TODO: move it (maybe) to onStop() method
		View mainContent = findViewById(R.id.main_content);
		if (mainContent != null) {
			mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
		}

		mDrawerLayout.closeDrawer(Gravity.START);
	}

	protected void setupActionBarToolbar() {
		if (mActionBarToolbar == null) {
			mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
			if (mActionBarToolbar != null) {
				setSupportActionBar(mActionBarToolbar);
			}
		}
	}

	protected Toolbar getActionBarToolbar() {
		return mActionBarToolbar;
	}

	private View makeNavDrawerItem(final int itemId, ViewGroup container) {
		boolean selected = getSelfNavDrawerItem() == itemId;
		int layoutToInflate = 0;
		if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
			layoutToInflate = R.layout.navdrawer_separator;
		} else {
			layoutToInflate = R.layout.navdrawer_item;
		}
		View view = getLayoutInflater().inflate(layoutToInflate, container, false);

		if (isSeparator(itemId)) {
			// we are done
			view.setClickable(false);
			view.setFocusable(false);
			view.setContentDescription("");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				//				view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
			}
			return view;
		}

		ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		TextView titleView = (TextView) view.findViewById(R.id.title);
		int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ? NAVDRAWER_ICON_RES_ID[itemId] : 0;
		int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ? NAVDRAWER_TITLE_RES_ID[itemId] : 0;

		// set icon and text
		iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
		if (iconId > 0) {
			iconView.setImageResource(iconId);
		}
		titleView.setText(getString(titleId));

		formatNavDrawerItem(view, itemId, selected);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNavDrawerItemClicked(itemId);
			}
		});

		return view;
	}

	private boolean isSpecialItem(int itemId) {
		return itemId == NAVDRAWER_ITEM_SETTINGS;
	}

	private boolean isSeparator(int itemId) {
		return itemId == NAVDRAWER_ITEM_SEPARATOR;
	}

	private void formatNavDrawerItem(View view, int itemId, boolean selected) {
		if (isSeparator(itemId)) {
			return;
		}

		ImageView iconView = (ImageView) view.findViewById(R.id.icon);
		TextView titleView = (TextView) view.findViewById(R.id.title);

		// configure its appearance according to whether or not it's selected
		titleView.setTextColor(selected ? getResources().getColor(R.color.navdrawer_text_color_selected)
				: getResources().getColor(R.color.navdrawer_text_color));
		iconView.setColorFilter(selected ? getResources().getColor(R.color.navdrawer_icon_tint_selected)
				: getResources().getColor(R.color.navdrawer_icon_tint));
	}

}
