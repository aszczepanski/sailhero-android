package put.sailhero.ui;

import put.sailhero.android.R;
import put.sailhero.model.Port;
import put.sailhero.provider.SailHeroContract;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

		int portId = getIntent().getIntExtra("port_id", -1);
		assert portId != -1;

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.main_content, new PortFragment(portId)).commit();
		}

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			// actionBar.setTitle(mPort.getName());
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

		private CheckBox mHasPowerConnectionCheckBox;
		private CheckBox mHasFuelStationCheckBox;

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

			mNameTextView = (TextView) rootView.findViewById(R.id.nameTextViewValue);
			mCityTextView = (TextView) rootView.findViewById(R.id.cityTextViewValue);
			mStreetTextView = (TextView) rootView.findViewById(R.id.streetTextViewValue);
			mWebsiteTextView = (TextView) rootView.findViewById(R.id.websiteTextViewValue);
			mTelephoneTextView = (TextView) rootView.findViewById(R.id.telephoneTextViewValue);

			mHasPowerConnectionCheckBox = (CheckBox) rootView.findViewById(R.id.hasPowerConnectionCheckBox);
			mHasFuelStationCheckBox = (CheckBox) rootView.findViewById(R.id.hasFuelStationCheckBox);

			return rootView;
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String[] projection = new String[] {
					SailHeroContract.Port.COLUMN_NAME_ID,
					SailHeroContract.Port.COLUMN_NAME_NAME,
					SailHeroContract.Port.COLUMN_NAME_CITY,
					SailHeroContract.Port.COLUMN_NAME_STREET,
					SailHeroContract.Port.COLUMN_NAME_WEBSITE,
					SailHeroContract.Port.COLUMN_NAME_TELEPHONE,
					SailHeroContract.Port.COLUMN_NAME_HAS_POWER_CONNECTION,
					SailHeroContract.Port.COLUMN_NAME_HAS_FUEL_STATION
			};

			Loader<Cursor> loader = null;
			loader = new CursorLoader(mContext, SailHeroContract.Port.CONTENT_URI.buildUpon()
					.appendPath(String.valueOf(id))
					.build(), projection, null, null, null);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (getActivity() == null) {
				return;
			}

			if (!data.moveToFirst()) {
				getActivity().finish();
				return;
			}

			Toast.makeText(mContext, "cursor load finished", Toast.LENGTH_SHORT).show();

			mPort.setId(data.getInt(0));
			mPort.setName(data.getString(1));
			mPort.setCity(data.getString(2));
			mPort.setStreet(data.getString(3));
			mPort.setWebsite(data.getString(4));
			mPort.setTelephone(data.getString(5));
			mPort.setHasPowerConnection(data.getInt(6) != 0);
			mPort.setHasFuelStation(data.getInt(7) != 0);

			((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(mPort.getName());

			mNameTextView.setText(mPort.getName());
			mCityTextView.setText(mPort.getCity());
			mStreetTextView.setText(mPort.getStreet());
			mWebsiteTextView.setText(mPort.getWebsite());
			mTelephoneTextView.setText(mPort.getTelephone());

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

			mHasPowerConnectionCheckBox.setChecked(mPort.isHasPowerConnection());
			mHasFuelStationCheckBox.setChecked(mPort.isHasFuelStation());

		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}

}
