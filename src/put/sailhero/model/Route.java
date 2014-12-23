package put.sailhero.model;

import java.util.Iterator;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.content.ContentValues;

public class Route extends BaseModel {

	private Integer mId;
	private String mName;

	private LinkedList<Pin> mPins;

	public Route() {
	}

	public Route(JSONObject routeObject) {
		this();

		setId(Integer.valueOf(routeObject.get("id").toString()));
		setName((String) routeObject.get("name"));

		LinkedList<Pin> pins = new LinkedList<Pin>();

		JSONArray pinsArray = (JSONArray) routeObject.get("pins");
		if (pinsArray != null) {
			for (int i = 0; i < pinsArray.size(); i++) {
				JSONObject pinObject = (JSONObject) pinsArray.get(i);
				Pin pin = new Pin(pinObject);

				pins.addLast(pin);
			}
		}

		setPins(pins);
	}

	public Integer getId() {
		return mId;
	}

	public void setId(Integer id) {
		mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public LinkedList<Pin> getPins() {
		return mPins;
	}

	public void setPins(LinkedList<Pin> pins) {
		mPins = pins;
	}

	public static class Pin {
		private Double mLatitude, mLongitude;

		public Pin() {
		}

		public Pin(JSONObject pinObject) {
			this();

			setLatitude(Double.valueOf(pinObject.get("latitude").toString()));
			setLongitude(Double.valueOf(pinObject.get("longitude").toString()));
		}

		public Double getLatitude() {
			return mLatitude;
		}

		public void setLatitude(Double latitude) {
			mLatitude = latitude;
		}

		public Double getLongitude() {
			return mLongitude;
		}

		public void setLongitude(Double longitude) {
			mLongitude = longitude;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pin)) {
				return false;
			}

			Pin pinToCompare = (Pin) o;

			return getLatitude().equals(pinToCompare.getLatitude())
					&& getLongitude().equals(pinToCompare.getLongitude());
		}
	}

	@Override
	public ContentValues toContentValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Route)) {
			return false;
		}

		Route routeToCompare = (Route) o;

		boolean theSamePins = getPins().size() == routeToCompare.getPins().size();
		Iterator<Pin> pinsIterator = getPins().listIterator();
		Iterator<Pin> pinsToCompareIterator = routeToCompare.getPins().listIterator();
		while (theSamePins && pinsIterator.hasNext() && pinsToCompareIterator.hasNext()) {
			Pin pin = pinsIterator.next();
			Pin pinToCompare = pinsToCompareIterator.next();

			if (!pin.equals(pinToCompare)) {
				theSamePins = false;
			}
		}

		return getId().equals(routeToCompare.getId()) && getName().equals(routeToCompare.getName()) && theSamePins;
	}
}
