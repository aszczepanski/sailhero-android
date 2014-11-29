package put.sailhero.ui;

import put.sailhero.Config;
import put.sailhero.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchFriendsFragment extends Fragment {

	public SearchFriendsFragment() {
		Log.d(Config.TAG, "SearchFriendsFragment()");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(Config.TAG, "onCreateView()");

		View rootView = inflater.inflate(R.layout.fragment_search_friends, container, false);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.d(Config.TAG, "onAttach()");
	}

	@Override
	public void onDetach() {
		super.onDetach();

		Log.d(Config.TAG, "onDetach()");
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d(Config.TAG, "onResume()");
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.d(Config.TAG, "onPause()");
	}
}
