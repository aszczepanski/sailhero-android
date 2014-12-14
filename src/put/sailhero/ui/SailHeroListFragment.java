package put.sailhero.ui;

import put.sailhero.R;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SailHeroListFragment extends ListFragment {
	private String mContentDescription;
	private View mRootView;

	public interface Listener {
		public void onFragmentViewCreated(ListFragment fragment);

		public void onFragmentAttached(SailHeroListFragment fragment);

		public void onFragmentDetached(SailHeroListFragment fragment);
	}

	public SailHeroListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_items_list, container, false);
		if (mContentDescription != null) {
			mRootView.setContentDescription(mContentDescription);
		}
		return mRootView;
	}

	public void setContentDescription(String desc) {
		mContentDescription = desc;
		if (mRootView != null) {
			mRootView.setContentDescription(mContentDescription);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (getActivity() instanceof Listener) {
			((Listener) getActivity()).onFragmentViewCreated(this);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (getActivity() instanceof Listener) {
			((Listener) getActivity()).onFragmentAttached(this);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (getActivity() instanceof Listener) {
			((Listener) getActivity()).onFragmentDetached(this);
		}
	}
}
