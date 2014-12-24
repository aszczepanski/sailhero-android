package put.sailhero.model;

import android.content.Context;
import android.content.Intent;

public abstract class PoiModel extends BaseModel {

	protected String mName;
	protected String mCity;

	public PoiModel() {
		super();
	}

	public PoiModel(Integer id, String name, String city) {
		super(id);

		mName = name;
		mCity = city;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	public void setCity(String city) {
		mCity = city;
	}

	public String getCity() {
		return mCity;
	}

	public abstract Intent getDetailsIntent(final Context context);
}
