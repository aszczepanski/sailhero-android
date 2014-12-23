package put.sailhero.model;

import org.json.simple.JSONObject;

import put.sailhero.provider.SailHeroContract;
import android.content.ContentValues;
import android.database.Cursor;

public class Region extends BaseModel {

	private Integer id;
	private String fullName;
	private String codeName;

	public Region() {
	}

	public Region(JSONObject regionObject) {
		this();

		setId(Integer.valueOf(regionObject.get("id").toString()));
		setFullName(regionObject.get("full_name").toString());
		setCodeName(regionObject.get("code_name").toString());
	}

	public Region(Cursor c) {
		setId(c.getInt(Query.REGION_ID));
		setFullName(c.getString(Query.REGION_FULL_NAME));
		setCodeName(c.getString(Query.REGION_CODE_NAME));
	}

	public interface Query {
		String[] PROJECTION = {
				SailHeroContract.Region.COLUMN_NAME_ID,
				SailHeroContract.Region.COLUMN_NAME_FULL_NAME,
				SailHeroContract.Region.COLUMN_NAME_CODE_NAME
		};

		int REGION_ID = 0;
		int REGION_FULL_NAME = 1;
		int REGION_CODE_NAME = 2;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(SailHeroContract.Region.COLUMN_NAME_ID, getId());
		values.put(SailHeroContract.Region.COLUMN_NAME_CODE_NAME, getCodeName());
		values.put(SailHeroContract.Region.COLUMN_NAME_FULL_NAME, getFullName());

		return values;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Region)) {
			return false;
		}

		Region regionToCompare = (Region) o;

		return getId().equals(regionToCompare.getId()) && getFullName().equals(regionToCompare.getFullName())
				&& getCodeName().equals(regionToCompare.getCodeName());
	}
}
