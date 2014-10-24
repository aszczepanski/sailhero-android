package put.sailhero.android.utils;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.location.Location;
import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class GetPortsResponseCreator implements ResponseCreator<GetPortsResponse> {

	GetPortsResponse getPortsResponse = new GetPortsResponse();
	
	@Override
	public GetPortsResponse createFrom(HttpResponse response) throws InvalidResourceOwnerException,
			SystemException, UnprocessableEntityException {
		int statusCode = response.getStatusCode();
		
		if (statusCode == 200) {
			try {			
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				LinkedList<Port> ports = new LinkedList<Port>();
				
				JSONArray portsArray = (JSONArray) obj.get("ports");
				for (int i=0; i<portsArray.size(); i++) {
					JSONObject portObject = (JSONObject) portsArray.get(i);
					Port port = new Port();
					port.setId(Integer.valueOf(portObject.get("id").toString()));
					port.setName(portObject.get("name").toString());
					Location portLocation = new Location("sailhero");
					portLocation.setLatitude(Double.valueOf(portObject.get("latitude").toString()));
					portLocation.setLongitude(Double.valueOf(portObject.get("longitude").toString()));		
					port.setLocation(portLocation);
					port.setWebsite(portObject.get("website").toString());
					port.setCity(portObject.get("city").toString());
					port.setStreet(portObject.get("street").toString());
					port.setTelephone(portObject.get("telephone").toString());
					port.setAdditionalInfo(portObject.get("additional_info").toString());
					port.setSpots(Integer.valueOf(portObject.get("spots").toString()));
					port.setDepth(Integer.valueOf(portObject.get("depth").toString()));
					port.setHasPowerConnection(Boolean.valueOf(portObject.get("has_power_connection").toString()));
					port.setHasWC(Boolean.valueOf(portObject.get("has_wc").toString()));
					port.setHasShower(Boolean.valueOf(portObject.get("has_shower").toString()));
					port.setHasWashbasin(Boolean.valueOf(portObject.get("has_washbasin").toString()));
					port.setHasDishes(Boolean.valueOf(portObject.get("has_dishes").toString()));
					port.setHasWifi(Boolean.valueOf(portObject.get("has_wifi").toString()));
					port.setHasParking(Boolean.valueOf(portObject.get("has_parking").toString()));
					port.setHasSlip(Boolean.valueOf(portObject.get("has_slip").toString()));
					port.setHasWashingMachine(Boolean.valueOf(portObject.get("has_washing_machine").toString()));
					port.setHasFuelStation(Boolean.valueOf(portObject.get("has_fuel_station").toString()));
					port.setHasEmptyingChemicalToilet(Boolean.valueOf(portObject.get("has_emptying_chemical_toilet").toString()));
					port.setPricePerPerson(Float.valueOf(portObject.get("price_per_person").toString()));
					port.setPricePowerConnection(Float.valueOf(portObject.get("price_power_connection").toString()));
					port.setPriceWC(Float.valueOf(portObject.get("price_wc").toString()));
					port.setPriceShower(Float.valueOf(portObject.get("price_shower").toString()));
					port.setPriceWashbasin(Float.valueOf(portObject.get("price_washbasin").toString()));
					port.setPriceDishes(Float.valueOf(portObject.get("price_dishes").toString()));
					port.setPriceWifi(Float.valueOf(portObject.get("price_wifi").toString()));
					port.setPriceWashingMachine(Float.valueOf(portObject.get("price_washing_machine").toString()));
					port.setPriceEmptyingChemicalToilet(Float.valueOf(portObject.get("price_emptying_chemical_toilet").toString()));
					port.setPriceParking(Float.valueOf(portObject.get("price_parking").toString()));

					ports.addLast(port);
				}
				
				getPortsResponse.setPorts(ports);

			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			} catch (NumberFormatException e) {
				throw new SystemException(e.getMessage());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			
		} else {
			throw new SystemException("Invalid status code");
		}

		return getPortsResponse;
	}

}
