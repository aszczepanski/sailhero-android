package put.sailhero.model;

import org.json.simple.JSONObject;

import put.sailhero.provider.SailHeroContract;
import put.sailhero.ui.PortActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.text.TextUtils;

public class Port extends PoiModel {

	private Double mLatitude;
	private Double mLongitude;
	private String mWebsite;
	private String mStreet;
	private String mTelephone;
	private String mAdditionalInfo;
	private Integer mSpots;
	private Integer mDepth;
	private Boolean mHasPowerConnection;
	private Boolean mHasWC;
	private Boolean mHasShower;
	private Boolean mHasWashbasin;
	private Boolean mHasDishes;
	private Boolean mHasWifi;
	private Boolean mHasParking;
	private Boolean mHasSlip;
	private Boolean mHasWashingMachine;
	private Boolean mHasFuelStation;
	private Boolean mHasEmptyingChemicalToilet;
	private Float mPricePerPerson;
	private Float mPricePowerConnection;
	private Float mPriceWC;
	private Float mPriceShower;
	private Float mPriceWashbasin;
	private Float mPriceDishes;
	private Float mPriceWifi;
	private Float mPriceWashingMachine;
	private Float mPriceEmptyingChemicalToilet;
	private Float mPriceParking;
	private String mCurrency;

	public Port() {
	}

	public Port(JSONObject portObject) {
		this();

		setId(Integer.valueOf(portObject.get("id").toString()));
		setName(portObject.get("name").toString());
		setLatitude(Double.valueOf(portObject.get("latitude").toString()));
		setLongitude(Double.valueOf(portObject.get("longitude").toString()));
		setWebsite(portObject.get("website").toString());
		setCity(portObject.get("city").toString());
		setStreet(portObject.get("street").toString());
		setTelephone(portObject.get("telephone").toString());
		setAdditionalInfo(portObject.get("additional_info").toString());
		setSpots(Integer.valueOf(portObject.get("spots").toString()));
		setDepth(Integer.valueOf(portObject.get("depth").toString()));
		setHasPowerConnection(Boolean.valueOf(portObject.get("has_power_connection").toString()));
		setHasWC(Boolean.valueOf(portObject.get("has_wc").toString()));
		setHasShower(Boolean.valueOf(portObject.get("has_shower").toString()));
		setHasWashbasin(Boolean.valueOf(portObject.get("has_washbasin").toString()));
		setHasDishes(Boolean.valueOf(portObject.get("has_dishes").toString()));
		setHasWifi(Boolean.valueOf(portObject.get("has_wifi").toString()));
		setHasParking(Boolean.valueOf(portObject.get("has_parking").toString()));
		setHasSlip(Boolean.valueOf(portObject.get("has_slip").toString()));
		setHasWashingMachine(Boolean.valueOf(portObject.get("has_washing_machine").toString()));
		setHasFuelStation(Boolean.valueOf(portObject.get("has_fuel_station").toString()));
		setHasEmptyingChemicalToilet(Boolean.valueOf(portObject.get("has_emptying_chemical_toilet").toString()));
		setPricePerPerson(Float.valueOf(portObject.get("price_per_person").toString()));
		setPricePowerConnection(Float.valueOf(portObject.get("price_power_connection").toString()));
		setPriceWC(Float.valueOf(portObject.get("price_wc").toString()));
		setPriceShower(Float.valueOf(portObject.get("price_shower").toString()));
		setPriceWashbasin(Float.valueOf(portObject.get("price_washbasin").toString()));
		setPriceDishes(Float.valueOf(portObject.get("price_dishes").toString()));
		setPriceWifi(Float.valueOf(portObject.get("price_wifi").toString()));
		setPriceWashingMachine(Float.valueOf(portObject.get("price_washing_machine").toString()));
		setPriceEmptyingChemicalToilet(Float.valueOf(portObject.get("price_emptying_chemical_toilet").toString()));
		setPriceParking(Float.valueOf(portObject.get("price_parking").toString()));
		setCurrency((String) portObject.get("currency"));
	}

	public Port(Cursor c) {
		setId(c.getInt(Query.PORT_ID));
		setName(c.getString(Query.PORT_NAME));
		setLongitude(c.getDouble(Query.PORT_LONGITUDE));
		setLatitude(c.getDouble(Query.PORT_LATITUDE));
		setStreet(c.getString(Query.PORT_STREET));
		setWebsite(c.getString(Query.PORT_WEBSITE));
		setCity(c.getString(Query.PORT_CITY));
		setTelephone(c.getString(Query.PORT_TELEPHONE));
		setAdditionalInfo(c.getString(Query.PORT_ADDITIONAL_INFO));
		setSpots(c.getInt(Query.PORT_SPOTS));
		setDepth(c.getInt(Query.PORT_DEPTH));
		setHasPowerConnection(c.getInt(Query.PORT_HAS_POWER_CONNECTION) != 0);
		setHasWC(c.getInt(Query.PORT_HAS_WC) != 0);
		setHasShower(c.getInt(Query.PORT_HAS_SHOWER) != 0);
		setHasWashbasin(c.getInt(Query.PORT_HAS_WASHBASIN) != 0);
		setHasDishes(c.getInt(Query.PORT_HAS_DISHES) != 0);
		setHasWifi(c.getInt(Query.PORT_HAS_WIFI) != 0);
		setHasParking(c.getInt(Query.PORT_HAS_PARKING) != 0);
		setHasSlip(c.getInt(Query.PORT_HAS_SLIP) != 0);
		setHasWashingMachine(c.getInt(Query.PORT_HAS_WASHING_MACHINE) != 0);
		setHasFuelStation(c.getInt(Query.PORT_HAS_FUEL_STATION) != 0);
		setHasEmptyingChemicalToilet(c.getInt(Query.PORT_HAS_EMPTYING_CHEMICAL_TOILET) != 0);
		setPricePerPerson(c.getFloat(Query.PORT_PRICE_PER_PERSON));
		setPricePowerConnection(c.getFloat(Query.PORT_PRICE_POWER_CONNECTION));
		setPriceWC(c.getFloat(Query.PORT_PRICE_WC));
		setPriceShower(c.getFloat(Query.PORT_PRICE_SHOWER));
		setPriceWashbasin(c.getFloat(Query.PORT_PRICE_WASHBASIN));
		setPriceDishes(c.getFloat(Query.PORT_PRICE_DISHES));
		setPriceWifi(c.getFloat(Query.PORT_PRICE_WIFI));
		setPriceWashingMachine(c.getFloat(Query.PORT_PRICE_WASHING_MACHINE));
		setPriceEmptyingChemicalToilet(c.getFloat(Query.PORT_PRICE_EMPTYING_CHEMICAL_TOILET));
		setPriceParking(c.getFloat(Query.PORT_PRICE_PARKING));
		setCurrency(c.getString(Query.PORT_CURRENCY));
	}

	public interface Query {
		String[] PROJECTION = {
				SailHeroContract.Port.COLUMN_NAME_ID,
				SailHeroContract.Port.COLUMN_NAME_NAME,
				SailHeroContract.Port.COLUMN_NAME_LONGITUDE,
				SailHeroContract.Port.COLUMN_NAME_LATITUDE,
				SailHeroContract.Port.COLUMN_NAME_WEBSITE,
				SailHeroContract.Port.COLUMN_NAME_CITY,
				SailHeroContract.Port.COLUMN_NAME_STREET,
				SailHeroContract.Port.COLUMN_NAME_TELEPHONE,
				SailHeroContract.Port.COLUMN_NAME_ADDITIONAL_INFO,
				SailHeroContract.Port.COLUMN_NAME_SPOTS,
				SailHeroContract.Port.COLUMN_NAME_DEPTH,
				SailHeroContract.Port.COLUMN_NAME_HAS_POWER_CONNECTION,
				SailHeroContract.Port.COLUMN_NAME_HAS_WC,
				SailHeroContract.Port.COLUMN_NAME_HAS_SHOWER,
				SailHeroContract.Port.COLUMN_NAME_HAS_WASHBASIN,
				SailHeroContract.Port.COLUMN_NAME_HAS_DISHES,
				SailHeroContract.Port.COLUMN_NAME_HAS_WIFI,
				SailHeroContract.Port.COLUMN_NAME_HAS_PARKING,
				SailHeroContract.Port.COLUMN_NAME_HAS_SLIP,
				SailHeroContract.Port.COLUMN_NAME_HAS_WASHING_MACHINE,
				SailHeroContract.Port.COLUMN_NAME_HAS_FUEL_STATION,
				SailHeroContract.Port.COLUMN_NAME_HAS_EMPTYING_CHEMICAL_TOILET,
				SailHeroContract.Port.COLUMN_NAME_PRICE_PER_PERSON,
				SailHeroContract.Port.COLUMN_NAME_PRICE_POWER_CONNECTION,
				SailHeroContract.Port.COLUMN_NAME_PRICE_WC,
				SailHeroContract.Port.COLUMN_NAME_PRICE_SHOWER,
				SailHeroContract.Port.COLUMN_NAME_PRICE_WASHBASIN,
				SailHeroContract.Port.COLUMN_NAME_PRICE_DISHES,
				SailHeroContract.Port.COLUMN_NAME_PRICE_WIFI,
				SailHeroContract.Port.COLUMN_NAME_PRICE_PARKING,
				SailHeroContract.Port.COLUMN_NAME_PRICE_WASHING_MACHINE,
				SailHeroContract.Port.COLUMN_NAME_PRICE_EMPTYING_CHEMICAL_TOILET,
				SailHeroContract.Port.COLUMN_NAME_CURRENCY
		};

		int PORT_ID = 0;
		int PORT_NAME = 1;
		int PORT_LONGITUDE = 2;
		int PORT_LATITUDE = 3;
		int PORT_WEBSITE = 4;
		int PORT_CITY = 5;
		int PORT_STREET = 6;
		int PORT_TELEPHONE = 7;
		int PORT_ADDITIONAL_INFO = 8;
		int PORT_SPOTS = 9;
		int PORT_DEPTH = 10;
		int PORT_HAS_POWER_CONNECTION = 11;
		int PORT_HAS_WC = 12;
		int PORT_HAS_SHOWER = 13;
		int PORT_HAS_WASHBASIN = 14;
		int PORT_HAS_DISHES = 15;
		int PORT_HAS_WIFI = 16;
		int PORT_HAS_PARKING = 17;
		int PORT_HAS_SLIP = 18;
		int PORT_HAS_WASHING_MACHINE = 19;
		int PORT_HAS_FUEL_STATION = 20;
		int PORT_HAS_EMPTYING_CHEMICAL_TOILET = 21;
		int PORT_PRICE_PER_PERSON = 22;
		int PORT_PRICE_POWER_CONNECTION = 23;
		int PORT_PRICE_WC = 24;
		int PORT_PRICE_SHOWER = 25;
		int PORT_PRICE_WASHBASIN = 26;
		int PORT_PRICE_DISHES = 27;
		int PORT_PRICE_WIFI = 28;
		int PORT_PRICE_PARKING = 29;
		int PORT_PRICE_WASHING_MACHINE = 30;
		int PORT_PRICE_EMPTYING_CHEMICAL_TOILET = 31;
		int PORT_CURRENCY = 32;
	}

	public Location getLocation() {
		Location location = new Location("sailhero");
		location.setLatitude(getLatitude());
		location.setLongitude(getLongitude());
		return location;
	}

	public void setLatitude(Double latitude) {
		mLatitude = latitude;
	}

	public Double getLatitude() {
		return mLatitude;
	}

	public void setLongitude(Double longitude) {
		mLongitude = longitude;
	}

	public Double getLongitude() {
		return mLongitude;
	}

	public String getWebsite() {
		return mWebsite;
	}

	public void setWebsite(String website) {
		mWebsite = website;
	}

	public String getStreet() {
		return mStreet;
	}

	public void setStreet(String street) {
		mStreet = street;
	}

	public String getTelephone() {
		return mTelephone;
	}

	public void setTelephone(String telephone) {
		mTelephone = telephone;
	}

	public String getAdditionalInfo() {
		return mAdditionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		mAdditionalInfo = additionalInfo;
	}

	public Integer getSpots() {
		return mSpots;
	}

	public void setSpots(Integer spots) {
		mSpots = spots;
	}

	public Integer getDepth() {
		return mDepth;
	}

	public void setDepth(Integer depth) {
		mDepth = depth;
	}

	public boolean isHasPowerConnection() {
		return mHasPowerConnection;
	}

	public void setHasPowerConnection(boolean hasPowerConnection) {
		mHasPowerConnection = hasPowerConnection;
	}

	public boolean isHasWC() {
		return mHasWC;
	}

	public void setHasWC(boolean hasWC) {
		mHasWC = hasWC;
	}

	public boolean isHasShower() {
		return mHasShower;
	}

	public void setHasShower(boolean hasShower) {
		mHasShower = hasShower;
	}

	public boolean isHasWashbasin() {
		return mHasWashbasin;
	}

	public void setHasWashbasin(boolean hasWashbasin) {
		mHasWashbasin = hasWashbasin;
	}

	public boolean isHasDishes() {
		return mHasDishes;
	}

	public void setHasDishes(boolean hasDishes) {
		mHasDishes = hasDishes;
	}

	public boolean isHasWifi() {
		return mHasWifi;
	}

	public void setHasWifi(boolean hasWifi) {
		mHasWifi = hasWifi;
	}

	public boolean isHasParking() {
		return mHasParking;
	}

	public void setHasParking(boolean hasParking) {
		mHasParking = hasParking;
	}

	public boolean isHasSlip() {
		return mHasSlip;
	}

	public void setHasSlip(boolean hasSlip) {
		mHasSlip = hasSlip;
	}

	public boolean isHasWashingMachine() {
		return mHasWashingMachine;
	}

	public void setHasWashingMachine(boolean hasWashingMachine) {
		mHasWashingMachine = hasWashingMachine;
	}

	public boolean isHasFuelStation() {
		return mHasFuelStation;
	}

	public void setHasFuelStation(boolean hasFuelStation) {
		mHasFuelStation = hasFuelStation;
	}

	public boolean isHasEmptyingChemicalToilet() {
		return mHasEmptyingChemicalToilet;
	}

	public void setHasEmptyingChemicalToilet(boolean hasEmptyingChemicalToilet) {
		mHasEmptyingChemicalToilet = hasEmptyingChemicalToilet;
	}

	public Float getPricePerPerson() {
		return mPricePerPerson;
	}

	public void setPricePerPerson(Float pricePerPerson) {
		mPricePerPerson = pricePerPerson;
	}

	public Float getPricePowerConnection() {
		return mPricePowerConnection;
	}

	public void setPricePowerConnection(Float pricePowerConnection) {
		mPricePowerConnection = pricePowerConnection;
	}

	public Float getPriceWC() {
		return mPriceWC;
	}

	public void setPriceWC(Float priceWC) {
		mPriceWC = priceWC;
	}

	public Float getPriceShower() {
		return mPriceShower;
	}

	public void setPriceShower(Float priceShower) {
		mPriceShower = priceShower;
	}

	public Float getPriceWashbasin() {
		return mPriceWashbasin;
	}

	public void setPriceWashbasin(Float priceWashbasin) {
		mPriceWashbasin = priceWashbasin;
	}

	public Float getPriceDishes() {
		return mPriceDishes;
	}

	public void setPriceDishes(Float priceDishes) {
		mPriceDishes = priceDishes;
	}

	public Float getPriceWifi() {
		return mPriceWifi;
	}

	public void setPriceWifi(Float priceWifi) {
		mPriceWifi = priceWifi;
	}

	public Float getPriceWashingMachine() {
		return mPriceWashingMachine;
	}

	public void setPriceWashingMachine(Float priceWashingMachine) {
		mPriceWashingMachine = priceWashingMachine;
	}

	public Float getPriceEmptyingChemicalToilet() {
		return mPriceEmptyingChemicalToilet;
	}

	public void setPriceEmptyingChemicalToilet(Float priceEmptyingChemicalToilet) {
		mPriceEmptyingChemicalToilet = priceEmptyingChemicalToilet;
	}

	public Float getPriceParking() {
		return mPriceParking;
	}

	public void setPriceParking(Float priceParking) {
		mPriceParking = priceParking;
	}

	public String getCurrency() {
		return mCurrency;
	}

	public void setCurrency(String currency) {
		mCurrency = currency;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(SailHeroContract.Port.COLUMN_NAME_ID, getId());
		values.put(SailHeroContract.Port.COLUMN_NAME_NAME, getName());
		values.put(SailHeroContract.Port.COLUMN_NAME_LATITUDE, getLatitude());
		values.put(SailHeroContract.Port.COLUMN_NAME_LONGITUDE, getLongitude());
		values.put(SailHeroContract.Port.COLUMN_NAME_WEBSITE, getWebsite());
		values.put(SailHeroContract.Port.COLUMN_NAME_CITY, getCity());
		values.put(SailHeroContract.Port.COLUMN_NAME_STREET, getStreet());
		values.put(SailHeroContract.Port.COLUMN_NAME_TELEPHONE, getTelephone());
		values.put(SailHeroContract.Port.COLUMN_NAME_ADDITIONAL_INFO, getAdditionalInfo());
		values.put(SailHeroContract.Port.COLUMN_NAME_SPOTS, getSpots());
		values.put(SailHeroContract.Port.COLUMN_NAME_DEPTH, getDepth());
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_POWER_CONNECTION, isHasPowerConnection() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_WC, isHasWC() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_SHOWER, isHasShower() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_WASHBASIN, isHasWashbasin() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_DISHES, isHasDishes() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_WIFI, isHasWifi() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_PARKING, isHasParking() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_SLIP, isHasParking() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_WASHING_MACHINE, isHasWashingMachine() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_FUEL_STATION, isHasFuelStation() ? 1 : 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_HAS_EMPTYING_CHEMICAL_TOILET, isHasEmptyingChemicalToilet() ? 1
				: 0);
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_PER_PERSON, getPricePerPerson());
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_POWER_CONNECTION, getPricePowerConnection());
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_WC, getPriceWC());
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_SHOWER, getPriceShower());
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_WASHBASIN, getPriceWashbasin());
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_DISHES, getPriceDishes());
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_WIFI, getPriceWifi());
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_PARKING, getPriceParking());
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_WASHING_MACHINE, getPriceWashingMachine());
		values.put(SailHeroContract.Port.COLUMN_NAME_PRICE_EMPTYING_CHEMICAL_TOILET, getPriceEmptyingChemicalToilet());
		values.put(SailHeroContract.Port.COLUMN_NAME_CURRENCY, getCurrency());

		return values;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Port)) {
			return false;
		}

		Port portToCompare = (Port) o;

		return getId().equals(portToCompare.getId()) && TextUtils.equals(getName(), portToCompare.getName())
				&& getLatitude().equals(portToCompare.getLatitude())
				&& getLongitude().equals(portToCompare.getLongitude())
				&& TextUtils.equals(getWebsite(), portToCompare.getWebsite())
				&& TextUtils.equals(getCity(), portToCompare.getCity())
				&& TextUtils.equals(getStreet(), portToCompare.getStreet())
				&& TextUtils.equals(getTelephone(), portToCompare.getTelephone())
				&& TextUtils.equals(getAdditionalInfo(), portToCompare.getAdditionalInfo())
				&& getSpots().equals(portToCompare.getSpots()) && getDepth().equals(portToCompare.getDepth());
	}

	@Override
	public Intent getDetailsIntent(Context context) {
		Intent intent = new Intent(context, PortActivity.class);
		intent.putExtra("port_id", getId().intValue());

		return intent;
	}
}
