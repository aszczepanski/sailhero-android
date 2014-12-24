package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.model.Port;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.sync.RetrievePortCostsRequestHelper;
import put.sailhero.ui.widget.ObservableScrollView;
import put.sailhero.util.SyncUtils;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PortActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
		ObservableScrollView.Callbacks {

	public final static String TAG = "sailhero";

	private static final float PHOTO_ASPECT_RATIO = 1.7777777f;

	private ObservableScrollView mScrollView;

	private View mHeaderBox;
	private View mDetailsContainer;

	private int mPhotoHeightPixels;
	private int mHeaderHeightPixels;

	private boolean mHasPhoto;
	private View mPhotoViewContainer;
	private ImageView mPhotoView;

	private int mPortId;
	private Port mPort;

	private TextView mPortTitleTextView;
	private TextView mPortSubtitleTextView;

	private TextView mNameTextView;
	private TextView mCityTextView;
	private TextView mStreetTextView;
	private TextView mWebsiteTextView;
	private TextView mTelephoneTextView;
	private TextView mAdditionalInfoTextView;
	private TextView mSpotsTextView;
	private TextView mDepthTextView;

	private CheckBox mHasPowerConnectionCheckBox;
	private CheckBox mHasWcCheckBox;
	private CheckBox mHasShowerCheckBox;
	private CheckBox mHasWashbasinCheckBox;
	private CheckBox mHasDishesCheckBox;
	private CheckBox mHasWifiCheckBox;
	private CheckBox mHasParkingCheckBox;
	private CheckBox mHasSlipCheckBox;
	private CheckBox mHasWashingMachineCheckBox;
	private CheckBox mHasFuelStationCheckBox;
	private CheckBox mHasEmptyingChemicalToiletCheckBox;

	private TextView mPricePowerConnectionTextView;
	private TextView mPriceWcTextView;
	private TextView mPriceShowerTextView;
	private TextView mPriceWashbasinTextView;
	private TextView mPriceDishesTextView;
	private TextView mPriceWifiTextView;
	private TextView mPriceWashingMachineTextView;
	private TextView mPriceEmptyingChemicalToiletTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_port);

		overridePendingTransition(0, 0);

		mPortId = getIntent().getIntExtra("port_id", -1);
		if (mPortId == -1) {
			Toast.makeText(this, "Port not found.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");

		mPortTitleTextView = (TextView) findViewById(R.id.port_title);
		mPortSubtitleTextView = (TextView) findViewById(R.id.port_subtitle);

		mNameTextView = (TextView) findViewById(R.id.name_text_view);
		mCityTextView = (TextView) findViewById(R.id.city_text_view);
		mStreetTextView = (TextView) findViewById(R.id.street_text_view);
		mWebsiteTextView = (TextView) findViewById(R.id.website_text_view);
		mTelephoneTextView = (TextView) findViewById(R.id.telephone_text_view);
		mAdditionalInfoTextView = (TextView) findViewById(R.id.additional_info_text_view);
		mSpotsTextView = (TextView) findViewById(R.id.spots_text_view);
		mDepthTextView = (TextView) findViewById(R.id.depth_text_view);

		mHasPowerConnectionCheckBox = (CheckBox) findViewById(R.id.has_power_connection_check_box);
		mHasWcCheckBox = (CheckBox) findViewById(R.id.has_wc_check_box);
		mHasShowerCheckBox = (CheckBox) findViewById(R.id.has_shower_check_box);
		mHasWashbasinCheckBox = (CheckBox) findViewById(R.id.has_washbasin_check_box);
		mHasDishesCheckBox = (CheckBox) findViewById(R.id.has_dishes_check_box);
		mHasWifiCheckBox = (CheckBox) findViewById(R.id.has_wifi_check_box);
		mHasParkingCheckBox = (CheckBox) findViewById(R.id.has_parking_check_box);
		mHasSlipCheckBox = (CheckBox) findViewById(R.id.has_slip_check_box);
		mHasWashingMachineCheckBox = (CheckBox) findViewById(R.id.has_washing_machine_check_box);
		mHasFuelStationCheckBox = (CheckBox) findViewById(R.id.has_fuel_station_check_box);
		mHasEmptyingChemicalToiletCheckBox = (CheckBox) findViewById(R.id.has_emptying_chemical_toilet_check_box);

		mPricePowerConnectionTextView = (TextView) findViewById(R.id.price_power_connection_text_view);
		mPriceWcTextView = (TextView) findViewById(R.id.price_wc_text_view);
		mPriceShowerTextView = (TextView) findViewById(R.id.price_shower_text_view);
		mPriceWashbasinTextView = (TextView) findViewById(R.id.price_washbasin_text_view);
		mPriceDishesTextView = (TextView) findViewById(R.id.price_dishes_text_view);
		mPriceWifiTextView = (TextView) findViewById(R.id.price_wifi_text_view);
		mPriceWashingMachineTextView = (TextView) findViewById(R.id.price_washing_machine_text_view);
		mPriceEmptyingChemicalToiletTextView = (TextView) findViewById(R.id.price_emptying_chemical_toilet_text_view);

		getLoaderManager().initLoader(mPortId, null, this);

		mHasPhoto = true;

		mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
		mScrollView.addCallbacks(this);
		ViewTreeObserver vto = mScrollView.getViewTreeObserver();
		if (vto.isAlive()) {
			vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
		}

		mDetailsContainer = findViewById(R.id.details_container);
		mHeaderBox = findViewById(R.id.header_session);
		mPhotoViewContainer = findViewById(R.id.session_photo_container);
		mPhotoView = (ImageView) findViewById(R.id.session_photo);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Loader<Cursor> loader = null;
		loader = new CursorLoader(PortActivity.this, SailHeroContract.Port.CONTENT_URI.buildUpon()
				.appendPath(String.valueOf(id))
				.build(), Port.Query.PROJECTION, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (!data.moveToFirst()) {
			Toast.makeText(PortActivity.this, "Port has been deleted from database.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		mPort = new Port(data);

		mPortTitleTextView.setText(mPort.getName());
		mPortSubtitleTextView.setText(mPort.getCity());

		mNameTextView.setText(mPort.getName());
		mCityTextView.setText(mPort.getCity());
		mStreetTextView.setText(mPort.getStreet());
		mAdditionalInfoTextView.setText(mPort.getAdditionalInfo());
		mSpotsTextView.setText(mPort.getSpots().toString());
		mDepthTextView.setText(mPort.getDepth().toString());

		mWebsiteTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPort.getWebsite()));
				startActivity(browserIntent);
			}
		});

		mTelephoneTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent telephoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPort.getTelephone()));
				startActivity(telephoneIntent);
			}
		});

		Log.d(TAG, "has wc: " + mPort.isHasWC());

		mHasPowerConnectionCheckBox.setChecked(mPort.isHasPowerConnection());
		if (mPort.isHasPowerConnection()) {
			mPricePowerConnectionTextView.setText(mPort.getPricePowerConnection() + " " + mPort.getCurrency());
		}
		mHasWcCheckBox.setChecked(mPort.isHasWC());
		if (mPort.isHasWC()) {
			mPriceWcTextView.setText(mPort.getPriceWC() + " " + mPort.getCurrency());
		}
		mHasShowerCheckBox.setChecked(mPort.isHasShower());
		if (mPort.isHasShower()) {
			mPriceShowerTextView.setText(mPort.getPriceShower() + " " + mPort.getCurrency());
		}
		mHasWashbasinCheckBox.setChecked(mPort.isHasWashbasin());
		if (mPort.isHasWashbasin()) {
			mPriceWashbasinTextView.setText(mPort.getPriceWashbasin() + " " + mPort.getCurrency());
		}
		mHasDishesCheckBox.setChecked(mPort.isHasDishes());
		if (mPort.isHasDishes()) {
			mPriceDishesTextView.setText(mPort.getPriceDishes() + " " + mPort.getCurrency());
		}
		mHasWifiCheckBox.setChecked(mPort.isHasWifi());
		if (mPort.isHasWifi()) {
			mPriceWifiTextView.setText(mPort.getPriceWifi() + " " + mPort.getCurrency());
		}
		mHasParkingCheckBox.setChecked(mPort.isHasParking());
		mHasSlipCheckBox.setChecked(mPort.isHasSlip());
		mHasWashingMachineCheckBox.setChecked(mPort.isHasWashingMachine());
		if (mPort.isHasWashingMachine()) {
			mPriceWashingMachineTextView.setText(mPort.getPriceWashingMachine() + " " + mPort.getCurrency());
		}
		mHasFuelStationCheckBox.setChecked(mPort.isHasFuelStation());
		mHasEmptyingChemicalToiletCheckBox.setChecked(mPort.isHasEmptyingChemicalToilet());
		if (mPort.isHasEmptyingChemicalToilet()) {
			mPriceEmptyingChemicalToiletTextView.setText(mPort.getPriceEmptyingChemicalToilet() + " "
					+ mPort.getCurrency());
		}

		RequestHelperAsyncTask calculatorTask = new RequestHelperAsyncTask(PortActivity.this,
				new RetrievePortCostsRequestHelper(PortActivity.this, mPort.getId()),
				new RequestHelperAsyncTask.AsyncRequestListener() {
					@Override
					public void onSuccess(RequestHelper requestHelper) {
						Log.i(TAG, "cost received");
					}
				});
		calculatorTask.execute();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private void recomputePhotoAndScrollingMetrics() {
		mHeaderHeightPixels = mHeaderBox.getHeight();

		mPhotoHeightPixels = 0;
		if (mHasPhoto) {
			mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
			mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 2 / 3);
		}

		ViewGroup.LayoutParams lp;
		lp = mPhotoViewContainer.getLayoutParams();
		if (lp.height != mPhotoHeightPixels) {
			lp.height = mPhotoHeightPixels;
			mPhotoViewContainer.setLayoutParams(lp);
		}

		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mDetailsContainer.getLayoutParams();
		if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
			mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
			mDetailsContainer.setLayoutParams(mlp);
		}

		onScrollChanged(0, 0); // trigger scroll handling
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mScrollView == null) {
			return;
		}

		ViewTreeObserver vto = mScrollView.getViewTreeObserver();
		if (vto.isAlive()) {
			vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
		}
	}

	private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			// mAddScheduleButtonHeightPixels = mAddScheduleButton.getHeight();
			recomputePhotoAndScrollingMetrics();
		}
	};

	@Override
	public void onScrollChanged(int deltaX, int deltaY) {
		// Reposition the header bar -- it's normally anchored to the top of the content,
		// but locks to the top of the screen on scroll
		int scrollY = mScrollView.getScrollY();

		float newTop = Math.max(mPhotoHeightPixels, scrollY);
		mHeaderBox.setTranslationY(newTop);
		//        mAddScheduleButton.setTranslationY(newTop + mHeaderHeightPixels
		//                - mAddScheduleButtonHeightPixels / 2);

		float gapFillProgress = 1;
		if (mPhotoHeightPixels != 0) {
			//            gapFillProgress = Math.min(Math.max(UIUtils.getProgress(scrollY, 0, mPhotoHeightPixels), 0), 1);
		}

		// Move background photo (parallax effect)
		mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
	}

	private static class RetrieveCostsAsyncTask extends AsyncTask<Void, Void, Void> {

		final private Context mContext;
		final private RetrievePortCostsRequestHelper mRequestHelper;
		final private TextView mCostsTextView;

		private Exception mException;

		public RetrieveCostsAsyncTask(final Context context, RetrievePortCostsRequestHelper requestHelper,
				TextView costsTextView) {
			mContext = context;
			mRequestHelper = requestHelper;

			mCostsTextView = costsTextView;
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

			// TODO: set text
		}
	}
}
