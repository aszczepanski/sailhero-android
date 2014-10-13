package put.sailhero.android.app;

import put.sailhero.android.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class AlertActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new AlertFragment())
					.commit();
		}
	}

	public static class AlertFragment extends Fragment {

		private Spinner mSpinner;
		private Button mSubmitAlertButton;

		public AlertFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_alert, container, false);

			mSpinner = (Spinner) rootView.findViewById(R.id.FragmentAlertSpinner);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
					R.array.alert_names, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinner.setAdapter(adapter);

			mSubmitAlertButton = (Button) rootView
					.findViewById(R.id.FragmentAlertSubmitAlertButton);
			mSubmitAlertButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String selectedAlert = mSpinner.getSelectedItem().toString();
					Toast.makeText(getActivity(), "Selected alert: " + selectedAlert, Toast.LENGTH_SHORT).show();
				}
			});

			return rootView;
		}
	}
}
