package put.sailhero.ui;

import put.sailhero.R;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class SailHeroListFragment extends ListFragment {
	private String mContentDescription;
	private View mRootView;
	private ListView mListView;

	public interface Listener {
		public void onFragmentViewCreated(ListFragment fragment);

		public void onFragmentAttached(SailHeroListFragment fragment);

		public void onFragmentDetached(SailHeroListFragment fragment);
	}

	public interface ScrollListener {
		public void onScrollStateChanged(AbsListView view, int scrollState);

		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
	}

	public SailHeroListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_items_list, container, false);
		if (mContentDescription != null) {
			mRootView.setContentDescription(mContentDescription);
		}

		mListView = (ListView) mRootView.findViewById(android.R.id.list);
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (getActivity() instanceof ScrollListener) {
					((ScrollListener) getActivity()).onScrollStateChanged(view, scrollState);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (getActivity() instanceof ScrollListener) {
					((ScrollListener) getActivity()).onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				}
			}
		});

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
