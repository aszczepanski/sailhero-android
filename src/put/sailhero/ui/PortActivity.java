package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.model.Port;
import put.sailhero.provider.SailHeroContract;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class PortActivity extends BaseActivity {

	public final static String TAG = "sailhero";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_port);

		overridePendingTransition(0, 0);

		int portId = getIntent().getIntExtra("port_id", -1);
		if (portId == -1) {
			Toast.makeText(this, "Port not found.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.main_content, new PortFragment(portId)).commit();
		}

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	public static class PortFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

		public final static String TAG = "sailhero";

		private Context mContext;

		private int mPortId;
		private Port mPort;

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

		public PortFragment(int portId) {
			mPortId = portId;
			mPort = new Port();
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mContext = getActivity();
		}

		@Override
		public void onResume() {
			super.onResume();

			getLoaderManager().restartLoader(mPortId, null, PortFragment.this);
		}

		@Override
		public void onPause() {
			super.onPause();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_port, container, false);

			mNameTextView = (TextView) rootView.findViewById(R.id.name_text_view);
			mCityTextView = (TextView) rootView.findViewById(R.id.city_text_view);
			mStreetTextView = (TextView) rootView.findViewById(R.id.street_text_view);
			mWebsiteTextView = (TextView) rootView.findViewById(R.id.website_text_view);
			mTelephoneTextView = (TextView) rootView.findViewById(R.id.telephone_text_view);
			mAdditionalInfoTextView = (TextView) rootView.findViewById(R.id.additional_info_text_view);
			mSpotsTextView = (TextView) rootView.findViewById(R.id.spots_text_view);
			mDepthTextView = (TextView) rootView.findViewById(R.id.depth_text_view);

			mHasPowerConnectionCheckBox = (CheckBox) rootView.findViewById(R.id.has_power_connection_check_box);
			mHasWcCheckBox = (CheckBox) rootView.findViewById(R.id.has_wc_check_box);
			mHasShowerCheckBox = (CheckBox) rootView.findViewById(R.id.has_shower_check_box);
			mHasWashbasinCheckBox = (CheckBox) rootView.findViewById(R.id.has_washbasin_check_box);
			mHasDishesCheckBox = (CheckBox) rootView.findViewById(R.id.has_dishes_check_box);
			mHasWifiCheckBox = (CheckBox) rootView.findViewById(R.id.has_wifi_check_box);
			mHasParkingCheckBox = (CheckBox) rootView.findViewById(R.id.has_parking_check_box);
			mHasSlipCheckBox = (CheckBox) rootView.findViewById(R.id.has_slip_check_box);
			mHasWashingMachineCheckBox = (CheckBox) rootView.findViewById(R.id.has_washing_machine_check_box);
			mHasFuelStationCheckBox = (CheckBox) rootView.findViewById(R.id.has_fuel_station_check_box);
			mHasEmptyingChemicalToiletCheckBox = (CheckBox) rootView.findViewById(R.id.has_emptying_chemical_toilet_check_box);

			return rootView;
		}

		private interface PortQuery {
			String[] PROJECTION = {
					SailHeroContract.Port.COLUMN_NAME_ID,
					SailHeroContract.Port.COLUMN_NAME_NAME,
					SailHeroContract.Port.COLUMN_NAME_LONGITUDE,
					SailHeroContract.Port.COLUMN_NAME_LATITUDE,
					SailHeroContract.Port.COLUMN_NAME_WEBSITE,
					SailHeroContract.Port.COLUMN_NAME_CITY,
					SailHeroContract.Port.COLUMN_NAME_STREET,
					SailHeroContract.Port.COLUMN_NAME_TELEPHONE,
					SailHeroContract.Port.COLUMN_NAME_ADDITIONAL_INFO,
					SailHeroContract.Port.COLUMN_NAME_SPOTS,
					SailHeroContract.Port.COLUMN_NAME_DEPTH,
					SailHeroContract.Port.COLUMN_NAME_HAS_POWER_CONNECTION,
					SailHeroContract.Port.COLUMN_NAME_HAS_WC,
					SailHeroContract.Port.COLUMN_NAME_HAS_SHOWER,
					SailHeroContract.Port.COLUMN_NAME_HAS_WASHBASIN,
					SailHeroContract.Port.COLUMN_NAME_HAS_DISHES,
					SailHeroContract.Port.COLUMN_NAME_HAS_WIFI,
					SailHeroContract.Port.COLUMN_NAME_HAS_PARKING,
					SailHeroContract.Port.COLUMN_NAME_HAS_SLIP,
					SailHeroContract.Port.COLUMN_NAME_HAS_WASHING_MACHINE,
					SailHeroContract.Port.COLUMN_NAME_HAS_FUEL_STATION,
					SailHeroContract.Port.COLUMN_NAME_HAS_EMPTYING_CHEMICAL_TOILET,
					SailHeroContract.Port.COLUMN_NAME_PRICE_PER_PERSON,
					SailHeroContract.Port.COLUMN_NAME_PRICE_POWER_CONNECTION,
					SailHeroContract.Port.COLUMN_NAME_PRICE_WC,
					SailHeroContract.Port.COLUMN_NAME_PRICE_SHOWER,
					SailHeroContract.Port.COLUMN_NAME_PRICE_WASHBASIN,
					SailHeroContract.Port.COLUMN_NAME_PRICE_DISHES,
					SailHeroContract.Port.COLUMN_NAME_PRICE_WIFI,
					SailHeroContract.Port.COLUMN_NAME_PRICE_PARKING,
					SailHeroContract.Port.COLUMN_NAME_PRICE_WASHING_MACHINE,
					SailHeroContract.Port.COLUMN_NAME_PRICE_EMPTYING_CHEMICAL_TOILET
			};

			int PORT_ID = 0;
			int PORT_NAME = 1;
			int PORT_LONGITUDE = 2;
			int PORT_LATITUDE = 3;
			int PORT_WEBSITE = 4;
			int PORT_CITY = 5;
			int PORT_STREET = 6;
			int PORT_TELEPHONE = 7;
			int PORT_ADDITIONAL_INFO = 8;
			int PORT_SPOTS = 9;
			int PORT_DEPTH = 10;
			int PORT_HAS_POWER_CONNECTION = 11;
			int PORT_HAS_WC = 12;
			int PORT_HAS_SHOWER = 13;
			int PORT_HAS_WASHBASIN = 14;
			int PORT_HAS_DISHES = 15;
			int PORT_HAS_WIFI = 16;
			int PORT_HAS_PARKING = 17;
			int PORT_HAS_SLIP = 18;
			int PORT_HAS_WASHING_MACHINE = 19;
			int PORT_HAS_FUEL_STATION = 20;
			int PORT_HAS_EMPTYING_CHEMICAL_TOILET = 21;
			int PORT_PRICE_PER_PERSON = 22;
			int PORT_PRICE_POWER_CONNECTION = 23;
			int PORT_PRICE_WC = 24;
			int PORT_PRICE_SHOWER = 25;
			int PORT_PRICE_WASHBASIN = 26;
			int PORT_PRICE_DISHES = 27;
			int PORT_PRICE_WIFI = 28;
			int PORT_PRICE_PARKING = 29;
			int PORT_PRICE_WASHING_MACHINE = 30;
			int PORT_PRICE_EMPTYING_CHEMICAL_TOILET = 31;
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Loader<Cursor> loader = null;
			loader = new CursorLoader(mContext, SailHeroContract.Port.CONTENT_URI.buildUpon()
					.appendPath(String.valueOf(id))
					.build(), PortQuery.PROJECTION, null, null, null);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (getActivity() == null) {
				return;
			}

			if (!data.moveToFirst()) {
				Toast.makeText(mContext, "Port has been deleted from database.", Toast.LENGTH_SHORT).show();
				getActivity().finish();
				return;
			}

			mPort.setId(data.getInt(PortQuery.PORT_ID));
			mPort.setName(data.getString(PortQuery.PORT_NAME));
			Location portLocation = new Location("sailhero");
			portLocation.setLongitude(data.getDouble(PortQuery.PORT_LONGITUDE));
			portLocation.setLatitude(data.getDouble(PortQuery.PORT_LATITUDE));
			mPort.setLocation(portLocation);
			mPort.setStreet(data.getString(PortQuery.PORT_STREET));
			mPort.setWebsite(data.getString(PortQuery.PORT_WEBSITE));
			mPort.setCity(data.getString(PortQuery.PORT_CITY));
			mPort.setTelephone(data.getString(PortQuery.PORT_TELEPHONE));
			mPort.setAdditionalInfo(data.getString(PortQuery.PORT_ADDITIONAL_INFO));
			mPort.setSpots(data.getInt(PortQuery.PORT_SPOTS));
			mPort.setDepth(data.getInt(PortQuery.PORT_DEPTH));
			mPort.setHasPowerConnection(data.getInt(PortQuery.PORT_HAS_POWER_CONNECTION) != 0);
			mPort.setHasWC(data.getInt(PortQuery.PORT_HAS_WC) != 0);
			mPort.setHasShower(data.getInt(PortQuery.PORT_HAS_SHOWER) != 0);
			mPort.setHasWashbasin(data.getInt(PortQuery.PORT_HAS_WASHBASIN) != 0);
			mPort.setHasDishes(data.getInt(PortQuery.PORT_HAS_DISHES) != 0);
			mPort.setHasWifi(data.getInt(PortQuery.PORT_HAS_WIFI) != 0);
			mPort.setHasParking(data.getInt(PortQuery.PORT_HAS_PARKING) != 0);
			mPort.setHasSlip(data.getInt(PortQuery.PORT_HAS_SLIP) != 0);
			mPort.setHasWashingMachine(data.getInt(PortQuery.PORT_HAS_WASHING_MACHINE) != 0);
			mPort.setHasFuelStation(data.getInt(PortQuery.PORT_HAS_FUEL_STATION) != 0);
			mPort.setHasEmptyingChemicalToilet(data.getInt(PortQuery.PORT_HAS_EMPTYING_CHEMICAL_TOILET) != 0);
			mPort.setPricePerPerson(data.getFloat(PortQuery.PORT_PRICE_PER_PERSON));
			mPort.setPricePowerConnection(data.getFloat(PortQuery.PORT_PRICE_POWER_CONNECTION));
			mPort.setPriceWC(data.getFloat(PortQuery.PORT_PRICE_WC));
			mPort.setPriceShower(data.getFloat(PortQuery.PORT_PRICE_SHOWER));
			mPort.setPriceWashbasin(data.getFloat(PortQuery.PORT_PRICE_WASHBASIN));
			mPort.setPriceDishes(data.getFloat(PortQuery.PORT_PRICE_DISHES));
			mPort.setPriceWifi(data.getFloat(PortQuery.PORT_PRICE_WIFI));
			mPort.setPriceWashingMachine(data.getFloat(PortQuery.PORT_PRICE_WASHING_MACHINE));
			mPort.setPriceEmptyingChemicalToilet(data.getFloat(PortQuery.PORT_PRICE_EMPTYING_CHEMICAL_TOILET));
			mPort.setPriceParking(data.getFloat(PortQuery.PORT_PRICE_PARKING));

			((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(mPort.getName());

			mNameTextView.setText(mPort.getName());
			mCityTextView.setText(mPort.getCity());
			mStreetTextView.setText(mPort.getStreet());
			mWebsiteTextView.setText(mPort.getWebsite());
			mTelephoneTextView.setText(mPort.getTelephone());
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
					Intent telephoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPort.getTelephone()));
					startActivity(telephoneIntent);
				}
			});

			Log.d(TAG, "has wc: " + mPort.isHasWC());

			mHasPowerConnectionCheckBox.setChecked(mPort.isHasPowerConnection());
			mHasWcCheckBox.setChecked(mPort.isHasWC());
			mHasShowerCheckBox.setChecked(mPort.isHasShower());
			mHasWashbasinCheckBox.setChecked(mPort.isHasWashbasin());
			mHasDishesCheckBox.setChecked(mPort.isHasDishes());
			mHasWifiCheckBox.setChecked(mPort.isHasWifi());
			mHasParkingCheckBox.setChecked(mPort.isHasParking());
			mHasSlipCheckBox.setChecked(mPort.isHasSlip());
			mHasWashingMachineCheckBox.setChecked(mPort.isHasWashingMachine());
			mHasFuelStationCheckBox.setChecked(mPort.isHasFuelStation());
			mHasEmptyingChemicalToiletCheckBox.setChecked(mPort.isHasEmptyingChemicalToilet());

		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}

}
