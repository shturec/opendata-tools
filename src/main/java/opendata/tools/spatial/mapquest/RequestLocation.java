package opendata.tools.spatial.mapquest;

import java.lang.reflect.Type;

import opendata.tools.data.Address;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

class RequestLocation {

	static final GsonBuilder gsonBuilder = new GsonBuilder();
	
	static {
	    gsonBuilder.registerTypeAdapter(Address.class, new RequestLocationSerializer());		
	}

	
	static class RequestLocationSerializer implements JsonSerializer<Address>{

		@Override
		public JsonElement serialize(Address address, Type type, JsonSerializationContext ctx) {
			
			JsonObject locationObj = new JsonObject();

			//TODO: check for minimum requirements for sending a proper geocode request
			String street = null;
			if((street = address.getStreetName())!=null){
				if(address.getStreetNumber()!=null){
					street= address.getStreetNumber() + " " + street;
				}
				if(street!=null)
					locationObj.addProperty("street", street);
			}
			
			if(address.getCity()!=null){
				locationObj.addProperty("city", address.getCity());
			}
			
			if(address.getPostalCode()>999 && address.getPostalCode()<10000){
				locationObj.addProperty("postalCode", address.getPostalCode());
			}
			
			locationObj.addProperty("country", "България");
			
			return locationObj;
		}
		
	}

	public String toJSONString(Address address){
		return gsonBuilder.create().toJson(address);
	}
	
	//each response result contains a "providedLocation" object referencing the provided location request json
	public Address fromJSON(JsonObject location){
		String city = null;
		JsonElement el = null;
		if((el = location.get("city"))!=null){
			city = el.getAsString();
			el = null;
		}
		String street = null;
		String streetName = null;
		String streetNumber = null;
		if((el = location.get("street"))!=null){
			street = el.getAsString();
			el = null;
			streetName = street;
			streetNumber = "";
			int pos=-1;
			if((pos = street.indexOf(" "))>0){
				streetNumber = street.substring(0, pos);
				streetName = street.substring(pos+1);
			}
		}
		int postalCode = 0; 
		if((el = location.get("postalCode"))!=null){
			postalCode = el.getAsInt();
			el = null;
		}
		return new Address(city, postalCode, streetName, streetNumber);
	}
}
