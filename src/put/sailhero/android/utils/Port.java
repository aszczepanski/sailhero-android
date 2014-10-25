package put.sailhero.android.utils;

import org.json.simple.JSONObject;

import android.location.Location;

public class Port {

	private Integer id;
	private String name;
	private Location location;
	private String website;
	private String city;
	private String street;
	private String telephone;
	private String additionalInfo;
	private Integer spots;
	private Integer depth;
	private Boolean hasPowerConnection;
	private Boolean hasWC;
	private Boolean hasShower;
	private Boolean hasWashbasin;
	private Boolean hasDishes;
	private Boolean hasWifi;
	private Boolean hasParking;
	private Boolean hasSlip;
	private Boolean hasWashingMachine;
	private Boolean hasFuelStation;
	private Boolean hasEmptyingChemicalToilet;
	private Float pricePerPerson;
	private Float pricePowerConnection;
	private Float priceWC;
	private Float priceShower;
	private Float priceWashbasin;
	private Float priceDishes;
	private Float priceWifi;
	private Float priceWashingMachine;
	private Float priceEmptyingChemicalToilet;
	private Float priceParking;

	public Port() {
	}

	public Port(JSONObject portObject) {
		this();

		setId(Integer.valueOf(portObject.get("id").toString()));
		setName(portObject.get("name").toString());
		Location portLocation = new Location("sailhero");
		portLocation.setLatitude(Double.valueOf(portObject.get("latitude").toString()));
		portLocation.setLongitude(Double.valueOf(portObject.get("longitude").toString()));
		setLocation(portLocation);
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
		setHasEmptyingChemicalToilet(Boolean.valueOf(portObject.get("has_emptying_chemical_toilet")
				.toString()));
		setPricePerPerson(Float.valueOf(portObject.get("price_per_person").toString()));
		setPricePowerConnection(Float.valueOf(portObject.get("price_power_connection").toString()));
		setPriceWC(Float.valueOf(portObject.get("price_wc").toString()));
		setPriceShower(Float.valueOf(portObject.get("price_shower").toString()));
		setPriceWashbasin(Float.valueOf(portObject.get("price_washbasin").toString()));
		setPriceDishes(Float.valueOf(portObject.get("price_dishes").toString()));
		setPriceWifi(Float.valueOf(portObject.get("price_wifi").toString()));
		setPriceWashingMachine(Float.valueOf(portObject.get("price_washing_machine").toString()));
		setPriceEmptyingChemicalToilet(Float.valueOf(portObject.get(
				"price_emptying_chemical_toilet").toString()));
		setPriceParking(Float.valueOf(portObject.get("price_parking").toString()));

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Integer getSpots() {
		return spots;
	}

	public void setSpots(Integer spots) {
		this.spots = spots;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public boolean isHasPowerConnection() {
		return hasPowerConnection;
	}

	public void setHasPowerConnection(boolean hasPowerConnection) {
		this.hasPowerConnection = hasPowerConnection;
	}

	public boolean isHasWC() {
		return hasWC;
	}

	public void setHasWC(boolean hasWC) {
		this.hasWC = hasWC;
	}

	public boolean isHasShower() {
		return hasShower;
	}

	public void setHasShower(boolean hasShower) {
		this.hasShower = hasShower;
	}

	public boolean isHasWashbasin() {
		return hasWashbasin;
	}

	public void setHasWashbasin(boolean hasWashbasin) {
		this.hasWashbasin = hasWashbasin;
	}

	public boolean isHasDishes() {
		return hasDishes;
	}

	public void setHasDishes(boolean hasDishes) {
		this.hasDishes = hasDishes;
	}

	public boolean isHasWifi() {
		return hasWifi;
	}

	public void setHasWifi(boolean hasWifi) {
		this.hasWifi = hasWifi;
	}

	public boolean isHasParking() {
		return hasParking;
	}

	public void setHasParking(boolean hasParking) {
		this.hasParking = hasParking;
	}

	public boolean isHasSlip() {
		return hasSlip;
	}

	public void setHasSlip(boolean hasSlip) {
		this.hasSlip = hasSlip;
	}

	public boolean isHasWashingMachine() {
		return hasWashingMachine;
	}

	public void setHasWashingMachine(boolean hasWashingMachine) {
		this.hasWashingMachine = hasWashingMachine;
	}

	public boolean isHasFuelStation() {
		return hasFuelStation;
	}

	public void setHasFuelStation(boolean hasFuelStation) {
		this.hasFuelStation = hasFuelStation;
	}

	public boolean isHasEmptyingChemicalToilet() {
		return hasEmptyingChemicalToilet;
	}

	public void setHasEmptyingChemicalToilet(boolean hasEmptyingChemicalToilet) {
		this.hasEmptyingChemicalToilet = hasEmptyingChemicalToilet;
	}

	public Float getPricePerPerson() {
		return pricePerPerson;
	}

	public void setPricePerPerson(Float pricePerPerson) {
		this.pricePerPerson = pricePerPerson;
	}

	public Float getPricePowerConnection() {
		return pricePowerConnection;
	}

	public void setPricePowerConnection(Float pricePowerConnection) {
		this.pricePowerConnection = pricePowerConnection;
	}

	public Float getPriceWC() {
		return priceWC;
	}

	public void setPriceWC(Float priceWC) {
		this.priceWC = priceWC;
	}

	public Float getPriceShower() {
		return priceShower;
	}

	public void setPriceShower(Float priceShower) {
		this.priceShower = priceShower;
	}

	public Float getPriceWashbasin() {
		return priceWashbasin;
	}

	public void setPriceWashbasin(Float priceWashbasin) {
		this.priceWashbasin = priceWashbasin;
	}

	public Float getPriceDishes() {
		return priceDishes;
	}

	public void setPriceDishes(Float priceDishes) {
		this.priceDishes = priceDishes;
	}

	public Float getPriceWifi() {
		return priceWifi;
	}

	public void setPriceWifi(Float priceWifi) {
		this.priceWifi = priceWifi;
	}

	public Float getPriceWashingMachine() {
		return priceWashingMachine;
	}

	public void setPriceWashingMachine(Float priceWashingMachine) {
		this.priceWashingMachine = priceWashingMachine;
	}

	public Float getPriceEmptyingChemicalToilet() {
		return priceEmptyingChemicalToilet;
	}

	public void setPriceEmptyingChemicalToilet(Float priceEmptyingChemicalToilet) {
		this.priceEmptyingChemicalToilet = priceEmptyingChemicalToilet;
	}

	public Float getPriceParking() {
		return priceParking;
	}

	public void setPriceParking(Float priceParking) {
		this.priceParking = priceParking;
	}

}
