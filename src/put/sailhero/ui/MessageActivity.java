package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.R.layout;
import android.os.Bundle;

public class MessageActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		overridePendingTransition(0, 0);
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_MESSAGES;
	}

}
