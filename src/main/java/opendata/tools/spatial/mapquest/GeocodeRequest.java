package opendata.tools.spatial.mapquest;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import opendata.tools.data.Address;

public class GeocodeRequest {

	private List<Address> addresses;
	
	public GeocodeRequest(List<Address> addresses) {
		this.addresses = addresses;
	}

	public String getLocations(){
		RequestLocation reqLocation = new RequestLocation();
		JsonArray array = new JsonArray();
		for (Address address : addresses) {
			String location = reqLocation.toJSONString(address);
			array.add(location);
		}
		JsonObject locations = new JsonObject();
		locations.add("locations", array);
		return locations.toString();
	}
	
}
