package put.sailhero.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.simple.JSONObject;

public class Message {

	private Integer mId;
	private String mBody;

	private Date mCreatedAt;
	// TODO: user data
	private Double mLatitude;
	private Double mLongitude;

	public Message() {
	}

	public Message(JSONObject messageObject) {
		this();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.UK);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = (String) messageObject.get("created_at"); // "2011-03-10T11:54:30.207Z";
		try {
			setCreatedAt(formatter.parse(date.substring(0, 24)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public Date getCreatedAt() {
		return mCreatedAt;
	}

	public void setCreatedAt(Date createdAt) {
		mCreatedAt = createdAt;
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
