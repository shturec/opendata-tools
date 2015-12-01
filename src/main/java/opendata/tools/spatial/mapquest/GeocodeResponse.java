package opendata.tools.spatial.mapquest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opendata.tools.data.Address;
import opendata.tools.spatial.SpatialAddress;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeocodeResponse {

	private JsonObject responseJson;
	
	public GeocodeResponse(String responseJsonString){
		JsonParser parser = new JsonParser();
		this.responseJson = parser.parse(responseJsonString).getAsJsonObject();
	}
	
	public class Info {
		
		private int statuscode;
		private List<String> messages;
		public Info(int statuscode, List<String> messages){
			this.statuscode = statuscode;
			this.messages = messages;
		}
		public int getStatuscode() {
			return statuscode;
		}
		public void setStatuscode(int statuscode) {
			this.statuscode = statuscode;
		}
		public List<String> getMessages() {
			return messages;
		}
		public void setMessages(List<String> messages) {
			this.messages = messages;
		}
		
	}

	public Info getInfo() {
		JsonObject info = this.responseJson.getAsJsonObject("info");
		int statusCode = info.get("statuscode").getAsInt();
		List<String> messages = new ArrayList<String>();
		if (statusCode > 0) {
			JsonArray messagesArray = info.get("messages").getAsJsonArray();
			for (JsonElement messageEl : messagesArray) {
				messages.add(messageEl.getAsString());
			}
		}
		return new Info(statusCode, messages);
	}
	
	public List<SpatialAddress> getResults(){
		List<SpatialAddress> spAddresses = new ArrayList<SpatialAddress>();
		JsonArray resultElements = this.responseJson.getAsJsonArray("results");
		for (Iterator<JsonElement> resultsIterator = resultElements.iterator(); resultsIterator.hasNext();) {

			JsonObject resultObject = (JsonObject) resultsIterator.next().getAsJsonObject();

			JsonObject providedLocation = resultObject.get("providedLocation").getAsJsonObject();
			RequestLocation reqLocation = new RequestLocation();
			Address providedAddress = reqLocation.fromJSON(providedLocation);

//			String id = providedLocation.get("id").getAsString();
			// System.out.println("provided location: " +
			// providedAddress+ "; id="+providedLocation.get("id"));

			JsonArray locations = resultObject.getAsJsonArray("locations");
			Iterator<JsonElement> locationsIterator = locations.iterator();
			if (locationsIterator.hasNext()) {// get just the first if any. maxResults=1 didn't work
				JsonObject location = (JsonObject) locationsIterator.next().getAsJsonObject();
				ResponseLocation responseLocation = new ResponseLocation();
				SpatialAddress spAddress = responseLocation.fromJSON(location);
				
				spAddress.setCity(providedAddress.getCity());
				spAddress.setPostalCode(providedAddress.getPostalCode());
				spAddress.setStreetName(providedAddress.getStreetName());
				spAddress.setStreetNumber(providedAddress.getStreetNumber());
				
				spAddresses.add(spAddress);
			}
		}
		return spAddresses;
	}
	
}
