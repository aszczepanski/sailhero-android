package put.sailhero.model;

import android.content.ContentValues;

public abstract class BaseModel {
	protected Integer mId;

	public BaseModel() {
	}

	public BaseModel(Integer id) {
		mId = id;
	}

	public Integer getId() {
		return mId;
	}

	public void setId(Integer id) {
		mId = id;
	}
	
	public abstract ContentValues toContentValues();
}
