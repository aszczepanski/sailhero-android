package put.sailhero.ui;

import java.util.ArrayList;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.gcm.GcmRegistrationAsyncTask;
import put.sailhero.model.Alert;
import put.sailhero.model.User;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.service.AlertService;
import put.sailhero.service.AlertService.LocalBinder;
import put.sailhero.util.AccountUtils;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.StringUtils;
import put.sailhero.util.UnitUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class BaseActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	public final static String TAG = "sailhero";

	protected static final int NAVDRAWER_ITEM_DASHBOARD = 0;
	protected static final int NAVDRAWER_ITEM_POI = 1;
	protected static final int NAVDRAWER_ITEM_MAP = 2;
	protected static final int NAVDRAWER_ITEM_WEATHER = 3;
	protected static final int NAVDRAWER_ITEM_PEOPLE = 4;
	protected static final int NAVDRAWER_ITEM_MESSAGES = 5;
	protected static final int NAVDRAWER_ITEM_SETTINGS = 6;
	protected static final int NAVDRAWER_ITEM_ABOUT = 7;
	protected static final int NAVDRAWER_ITEM_INVALID = -1;
	protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;

	private static final int[] NAVDRAWER_TITLE_RES_ID = new int[] {
			R.string.navdrawer_item_dashboard,
			R.string.navdrawer_item_poi,
			R.string.navdrawer_item_map,
			R.string.navdrawer_item_weather,
			R.string.navdrawer_item_people,
			R.string.navdrawer_item_messages,
			R.string.navdrawer_item_settings,
			R.string.navdrawer_item_about
	};

	private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_drawer_map,
			R.drawable.ic_launcher,
			R.drawable.ic_drawer_people_met,
			R.drawable.ic_drawer_social,
			R.drawable.ic_drawer_settings,
			R.drawable.ic_launcher
	};

	private static final int NAVDRAWER_LAUNCH_DELAY = 250;

	private Handler mHandler;
	private Runnable mDeferredOnDrawerClosedRunnable;

	private ViewGroup mDrawerItemsListContainer;
	private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();
	private View[] mNavDrawerItemViews = null;

	private Toolbar mActionBarToolbar;
	private DrawerLayout mDrawerLayout;

	private AlertService mAlertService;
	private boolean mAlertServiceBound = false;

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private Object mSyncObserverHandle;
	private boolean mManualSyncRequest;

	private View mAlertBarToolbar;
	private Button mRespondToAlertButton;
	private TextView mAlertBarTypeTextView;
	private TextView mAlertBarDistanceTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new Handler();

		Intent alertServiceIntent = new Intent(this, AlertService.class);
		bindService(alertServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		sp.registerOnSharedPreferenceChangeListener(this);

		AccountManager.get(this).addOnAccountsUpdatedListener(mOnAccountsUpdateListener, null, true);
	}

	OnAccountsUpdateListener mOnAccountsUpdateListener = new OnAccountsUpdateListener() {
		@Override
		public void onAccountsUpdated(Account[] accounts) {
			AccountUtils.finishActivityAndStartLoginActivityIfNeeded(BaseActivity.this, BaseActivity.this);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindService(mConnection);

		AccountManager.get(this).removeOnAccountsUpdatedListener(mOnAccountsUpdateListener);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		sp.unregisterOnSharedPreferenceChangeListener(this);
	}

	private void setupAlertBar() {
		mAlertBarToolbar = findViewById(R.id.toolbar_alertbar);

		if (mAlertBarToolbar == null) {
			Log.w(TAG, "alert bar not found");
			return;
		}

		mRespondToAlertButton = (Button) findViewById(R.id.button_respond);
		mRespondToAlertButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Alert alertToRespond = PrefUtils.getClosestAlertToRespond(BaseActivity.this);

				if (alertToRespond == null) {
					return;
				}

				DialogFragment alertResponseDialogFragment = new AlertResponseDialogFragment(BaseActivity.this,
						alertToRespond);
				alertResponseDialogFragment.show(getFragmentManager(), "response");
			}

		});

		mAlertBarTypeTextView = (TextView) findViewById(R.id.alert_bar_type_text_view);
		mAlertBarDistanceTextView = (TextView) findViewById(R.id.alert_bar_distance_text_view);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to AlertService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mAlertService = binder.getService();
			mAlertServiceBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mAlertServiceBound = false;
		}
	};

	protected void updateAlertBarToolbar(Location location, Alert alertToRespond) {
		if (mAlertBarToolbar == null) {
			return;
		}

		if (location != null && alertToRespond != null
				&& location.distanceTo(alertToRespond.getLocation()) < PrefUtils.getAlertRadius(this)) {
			mAlertBarTypeTextView.setText(StringUtils.getStringForAlertType(this, alertToRespond.getAlertType()));
			Integer displayedDistanceToAlert = UnitUtils.roundDistanceTo25(location.distanceTo(alertToRespond.getLocation()));
			mAlertBarDistanceTextView.setText(getResources().getQuantityString(R.plurals.alert_distance_in_metres,
					displayedDistanceToAlert, displayedDistanceToAlert));
			mAlertBarToolbar.setVisibility(View.VISIBLE);
		} else {
			mAlertBarToolbar.setVisibility(View.GONE);
		}
	}

	protected void onClosestAlertToRespondUpdate(Alert alertToRespond) {
		Location lastKnownLocation = PrefUtils.getLastKnownLocation(this);

		updateAlertBarToolbar(lastKnownLocation, alertToRespond);
	}

	protected void onClosestAlertUpdate(Alert alert) {
	}

	protected void onLastKnownLocationUpdate(Location location) {
		Alert alertToRespond = PrefUtils.getClosestAlertToRespond(this);

		updateAlertBarToolbar(location, alertToRespond);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (AccountUtils.finishActivityAndStartLoginActivityIfNeeded(BaseActivity.this, BaseActivity.this)) {
			return;
		}

		mSyncStatusObserver.onStatusChanged(0);
		final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
		mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);

		String gcmRegistrationId = PrefUtils.getGcmRegistrationId(BaseActivity.this);
		if (gcmRegistrationId == null) {
			GcmRegistrationAsyncTask gcmRegistrationTask = new GcmRegistrationAsyncTask(BaseActivity.this);
			gcmRegistrationTask.execute();
		}

		onLastKnownLocationUpdate(PrefUtils.getLastKnownLocation(BaseActivity.this));
		onClosestAlertUpdate(PrefUtils.getClosestAlert(BaseActivity.this));

		if (mAlertBarToolbar != null) {
			onClosestAlertToRespondUpdate(PrefUtils.getClosestAlertToRespond(BaseActivity.this));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mSyncObserverHandle != null) {
			ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
			mSyncObserverHandle = null;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		int itemId = getSelfNavDrawerItem();
		if (itemId != NAVDRAWER_ITEM_INVALID) {
			setSelectedNavDrawerItem(itemId);
		}
	}

	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_INVALID;
	}

	private void trySetupSwipeRefresh() {
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2,
					R.color.refresh_progress_3);
			mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					requestDataRefresh();
				}
			});
		}
	}

	protected void requestDataRefresh() {
		mManualSyncRequest = true;
		Log.d(TAG, "Requesting manual data refresh.");
	}

	private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
		@Override
		public void onStatusChanged(final int which) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Account account = AccountUtils.getActiveAccount(BaseActivity.this);
					if (account == null) {
						onRefreshingStateChanged(false);
						mManualSyncRequest = false;
						return;
					}

					boolean syncActive = ContentResolver.isSyncActive(account, SailHeroContract.CONTENT_AUTHORITY);
					boolean syncPending = ContentResolver.isSyncPending(account, SailHeroContract.CONTENT_AUTHORITY);
					Log.d(TAG, "which: " + Integer.toString(which) + ", " + "syncActive: " + syncActive
							+ ", syncPending: " + syncPending);
					if (!syncActive && !syncPending) {
						mManualSyncRequest = false;
					}
					onRefreshingStateChanged(syncActive || (mManualSyncRequest && syncPending));
				}
			});
		}
	};

	protected void onRefreshingStateChanged(boolean refreshing) {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(refreshing);
		}
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
	}

	public void setupAccountBox() {
		if (mDrawerLayout == null) {
			return;
		}

		TextView nameTextView = (TextView) findViewById(R.id.profile_name_text);
		TextView emailTextView = (TextView) findViewById(R.id.profile_email_text);

		ImageView profileImageView = (ImageView) findViewById(R.id.profile_image);

		User currentUser = PrefUtils.getUser(BaseActivity.this);
		if (currentUser == null) {
			nameTextView.setVisibility(View.GONE);
			emailTextView.setVisibility(View.GONE);
			profileImageView.setVisibility(View.GONE);
		} else {
			nameTextView.setVisibility(View.VISIBLE);
			nameTextView.setText(currentUser.getName() + " " + currentUser.getSurname());

			emailTextView.setVisibility(View.VISIBLE);
			emailTextView.setText(currentUser.getEmail());

			profileImageView.setVisibility(View.VISIBLE);
			if (currentUser.getAvatarUrl() != null) {
				Glide.with(BaseActivity.this)
						.load(currentUser.getAvatarUrl())
						.asBitmap()
						.error(R.drawable.person_image_empty)
						.into(profileImageView);
			} else {
				Glide.with(BaseActivity.this).load(R.drawable.person_image_empty).asBitmap().into(profileImageView);
			}
		}

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

		mNavDrawerItems.add(NAVDRAWER_ITEM_DASHBOARD);
		mNavDrawerItems.add(NAVDRAWER_ITEM_POI);
		mNavDrawerItems.add(NAVDRAWER_ITEM_MAP);
		mNavDrawerItems.add(NAVDRAWER_ITEM_WEATHER);

		mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);

		mNavDrawerItems.add(NAVDRAWER_ITEM_PEOPLE);
		mNavDrawerItems.add(NAVDRAWER_ITEM_MESSAGES);

		mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);

		mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);
		mNavDrawerItems.add(NAVDRAWER_ITEM_ABOUT);

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
		setupAccountBox();
		setupAlertBar();

		trySetupSwipeRefresh();

		// TODO: setup other things
	}

	private void goToNavDrawerItem(int item) {
		Intent intent = null;
		switch (item) {
		case NAVDRAWER_ITEM_DASHBOARD:
			intent = new Intent(this, DashboardActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			break;
		case NAVDRAWER_ITEM_POI:
			intent = new Intent(this, PoiActivity.class);
			break;
		case NAVDRAWER_ITEM_MAP:
			intent = new Intent(this, MapActivity.class);
			break;
		case NAVDRAWER_ITEM_WEATHER:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(Config.WEATHER_URL));
			break;
		case NAVDRAWER_ITEM_PEOPLE:
			intent = new Intent(this, PeopleActivity.class);
			break;
		case NAVDRAWER_ITEM_MESSAGES:
			intent = new Intent(this, MessageActivity.class);
			break;
		case NAVDRAWER_ITEM_SETTINGS:
			intent = new Intent(this, PreferenceActivity.class);
			break;
		case NAVDRAWER_ITEM_ABOUT:
			intent = new Intent(this, AboutActivity.class);
			break;
		default:
			return;
		}

		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		if (getSelfNavDrawerItem() != NAVDRAWER_ITEM_DASHBOARD && !isSpecialItem(item)) {
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
		return (itemId == NAVDRAWER_ITEM_SETTINGS || itemId == NAVDRAWER_ITEM_ABOUT || itemId == NAVDRAWER_ITEM_WEATHER);
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

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(PrefUtils.PREF_CLOSEST_ALERT)) {
			onClosestAlertUpdate(PrefUtils.getClosestAlert(BaseActivity.this));
		} else if (key.equals(PrefUtils.PREF_CLOSEST_ALERT_TO_RESPOND)) {
			onClosestAlertToRespondUpdate(PrefUtils.getClosestAlertToRespond(BaseActivity.this));
		} else if (key.equals(PrefUtils.PREF_LAST_KNOWN_LOCATION)) {
			onLastKnownLocationUpdate(PrefUtils.getLastKnownLocation(BaseActivity.this));
		} else if (key.equals(PrefUtils.PREF_USER)) {
			setupAccountBox();
		}
	}
}
