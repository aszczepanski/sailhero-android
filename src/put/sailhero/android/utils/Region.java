package put.sailhero.android.utils;

import org.json.simple.JSONObject;

public class Region {

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
}
