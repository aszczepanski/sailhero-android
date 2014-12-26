package put.sailhero.model;

import org.json.simple.JSONObject;

public class Message {

	private Integer mId;
	private String mBody;
	// TODO: created at
	// TODO: user data
	private Double mLatitude;
	private Double mLongitude;

	public Message() {
	}

	public Message(JSONObject messageObject) {
		this();

		setId(Integer.valueOf(messageObject.get("id").toString()));
		setBody((String) messageObject.get("body"));
		setLatitude(Double.valueOf(messageObject.get("latitude").toString()));
		setLongitude(Double.valueOf(messageObject.get("longitude").toString()));
	}

	public Integer getId() {
		return mId;
	}

	public void setId(Integer id) {
		mId = id;
	}

	public String getBody() {
		return mBody;
	}

	public void setBody(String body) {
		mBody = body;
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
		if (!(o instanceof Message)) {
			return false;
		}

		Message messageToCompare = (Message) o;

		return getId().equals(messageToCompare.getId()) && getBody().equals(messageToCompare.getBody())
				&& getLatitude().equals(messageToCompare.getLatitude())
				&& getLongitude().equals(messageToCompare.getLongitude());
	}
}
