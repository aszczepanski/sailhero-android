package put.sailhero.android.app;

import java.util.AbstractList;

import put.sailhero.android.R;
import put.sailhero.android.util.SailHeroService;
import put.sailhero.android.util.SailHeroSettings;
import put.sailhero.android.util.model.Port;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class PortActivity extends Activity {

	public final static String TAG = "sailhero";

	private SailHeroService mService;
	private SailHeroSettings mSettings;

	private Port mPort;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_port);

		mService = SailHeroService.getInstance();
		mSettings = mService.getSettings();

		int portId = getIntent().getIntExtra("port_id", -1);
		assert (portId != -1);

		AbstractList<Port> ports = mSettings.getPorts();
		for (Port port : ports) {
			if (port.getId() == portId) {
				mPort = port;
				break;
			}
		}

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PortFragment(mPort))
					.commit();
		}
	}

	public static class PortFragment extends Fragment {

		public final static String TAG = "sailhero";

		private SailHeroService mService;
		private SailHeroSettings mSettings;

		private Port mPort;

		private TextView mNameTextView;
		private TextView mCityTextView;
		private TextView mStreetTextView;
		private TextView mWebsiteTextView;
		private TextView mTelephoneTextView;

		private CheckBox mHasPowerConnectionCheckBox;
		private CheckBox mHasFuelStationCheckBox;

		public PortFragment(Port port) {
			mPort = port;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_port, container, false);

			mNameTextView = (TextView) rootView.findViewById(R.id.nameTextViewValue);
			mCityTextView = (TextView) rootView.findViewById(R.id.cityTextViewValue);
			mStreetTextView = (TextView) rootView.findViewById(R.id.streetTextViewValue);
			mWebsiteTextView = (TextView) rootView.findViewById(R.id.websiteTextViewValue);
			mTelephoneTextView = (TextView) rootView.findViewById(R.id.telephoneTextViewValue);

			mNameTextView.setText(mPort.getName());
			mCityTextView.setText(mPort.getCity());
			mStreetTextView.setText(mPort.getStreet());
			mWebsiteTextView.setText(mPort.getWebsite());
			mTelephoneTextView.setText(mPort.getTelephone());

			mWebsiteTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPort
							.getWebsite()));
					startActivity(browserIntent);
				}
			});

			mTelephoneTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent telephoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
							+ mPort.getTelephone()));
					startActivity(telephoneIntent);
				}
			});

			mHasPowerConnectionCheckBox = (CheckBox) rootView
					.findViewById(R.id.hasPowerConnectionCheckBox);
			mHasFuelStationCheckBox = (CheckBox) rootView.findViewById(R.id.hasFuelStationCheckBox);

			mHasPowerConnectionCheckBox.setChecked(mPort.isHasPowerConnection());
			mHasFuelStationCheckBox.setChecked(mPort.isHasFuelStation());

			return rootView;
		}
	}
}
