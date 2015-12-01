package opendata.tools.spatial.mapquest;

import opendata.tools.spatial.SpatialAddress;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class ResponseLocation {
	
	public SpatialAddress fromJSON(JsonObject location){
		SpatialAddress spAddress = new SpatialAddress();
		JsonElement el = null;
		if((el=location.get("adminArea5"))!=null){
			spAddress.setCity(el.getAsString());
			el=null;
		}
		if((el=location.get("postalCode"))!=null){
			if(el.getAsString().length()>0)
				spAddress.setPostalCode(el.getAsInt());
			el=null;
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
			spAddress.setStreetName(streetName);
			spAddress.setStreetNumber(streetNumber);
		}
		JsonObject latLng = location.getAsJsonObject("latLng");
		if (latLng != null) {
			spAddress.setLatitude(latLng.get("lat").getAsString());
			spAddress.setLongitude(latLng.get("lng").getAsString());
		}
		return spAddress;
	}

}
