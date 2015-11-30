package opendata.tools.spatial;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opendata.tools.data.Address;
import opendata.tools.data.AddressValidator.Validation;
import opendata.tools.data.BasicAddressValidator;
import opendata.tools.http.AsyncHttpClient;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MapquestGeocodingService implements GeocodingServiceDelegate{

	String apiKey;
	String serviceUrl;
	BasicAddressValidator addressValidator;
	
	public MapquestGeocodingService(String serviceUrl, String apiKey) {
		this.serviceUrl = serviceUrl;
		this.apiKey = apiKey;
		this.addressValidator = new BasicAddressValidator();
	}

	@Override
	public Validation validateAddress(Address address) {
		return this.addressValidator.validate(address);
	}

	@Override
	public Promise<List<SpatialAddress>,Throwable,Float> geocodeBatch(List<Address> addresses) {
		String locations = "";
		for (Address address: addresses) {
			locations+=this.toJSON(address) + ",";
		}
		String url = null;
		try {
			url = this.getUrl(locations);
			return this.sendAsyncSvcRequest(url, new GeocodeResponseHandler());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Promise<List<SpatialAddress>,Throwable,Float> geocode(Address address) {
		String location = this.toJSON(address);
		String url = null;
		try {
			location = "&json={\"location\":" + URLEncoder.encode(location, StandardCharsets.UTF_8.name());
			url = serviceUrl.toString() + "/address?key=" + this.apiKey + "&maxResults=1" + location;
			//System.out.println(url);
			return this.sendAsyncSvcRequest(url, new GeocodeResponseHandler());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return null;//TODO
	}
	
	private Promise<List<SpatialAddress>,Throwable,Float> sendAsyncSvcRequest(String url, final GeocodeResponseHandler handler){
		final DeferredObject<List<SpatialAddress>,Throwable,Float> defered = new DeferredObject<List<SpatialAddress>, Throwable, Float>();
		AsyncHttpClient client = new AsyncHttpClient();
		try {
			client.get(url, new AsyncHttpClient.Callback<String>() {

				@Override
				public void done(String entity) {
					List<SpatialAddress> spAddress = handler.transform(entity);
					defered.resolve(spAddress);
				}

				@Override
				public void error(Throwable t) {
					defered.reject(t);
				}
			});				
			return defered.promise();
		} catch (IOException e) {
			e.printStackTrace();//log and continue
		}
		return null;
	}
	

	String toJSON(Address address) {
		//TODO: check for minimum requirements for sending a proper geocode request
		String segment = "";
		if(address.streetName!=null){
			segment=address.streetName;
			if(address.streetNumber!=null){
				segment = address.streetNumber + " " + segment;
			}
			segment = "\"street\":" + segment;
		}
		if(address.city!=null){
			if(segment.length()>0)
				segment+=",";
			segment+= "\"city\":" + address.city;
		}
		if(address.postalCode>999 && address.postalCode<10000){
			if(segment.length()>0)
				segment+=",";
			segment+= "\"postalCode\":" + address.postalCode;
		}
		if(segment.length()>0)
			segment+=",";
		segment+= "\"country\":" + "България";
		/*if(data!=null && !data.isEmpty()){
			if(segment.length()>0)
				segment+=",";
			int i = data.size();
			for (Map.Entry<String, String> entry : data.entrySet()) {
				segment += "\""+entry.getKey()+"\":\""+entry.getValue()+"\"";
				if(i>0){
					segment+=",";
				}
				i++;
			}
		}*/
		segment = "{" + segment + "}";
		return segment;
	}
	
	class GeocodeResponseHandler {
		List<SpatialAddress> transform(String data) {
			List<SpatialAddress> spAddresses = new ArrayList<SpatialAddress>();
			try {
				// TODO: move to mapquest response parser
				JsonParser parser = new JsonParser();
				JsonObject obj = parser.parse(data).getAsJsonObject();

				JsonObject info = obj.getAsJsonObject("info");
				int statusCode = info.get("statuscode").getAsInt();
				if (statusCode == 400) {
					JsonArray messagesArray = info.get("messages")
							.getAsJsonArray();
					String messages = "";
					for (JsonElement messageEl : messagesArray) {
						messages += messageEl.getAsString() + "; ";
					}
					System.err.println("Geocoding failed: " + messages);
				} else {
					JsonArray resultElements = obj.getAsJsonArray("results");
					for (Iterator<JsonElement> resultsIterator = resultElements.iterator(); resultsIterator.hasNext();) {

						JsonObject resultObject = (JsonObject) resultsIterator.next().getAsJsonObject();

						//JsonObject providedLocation = resultObject.get("providedLocation").getAsJsonObject();
						//Address providedAddress = addressFromProvidedLocationJSON(providedLocation);

//						String id = providedLocation.get("id").getAsString();
						// System.out.println("provided location: " +
						// providedAddress+ "; id="+providedLocation.get("id"));

						JsonArray locations = resultObject.getAsJsonArray("locations");
						Iterator<JsonElement> locationsIterator = locations.iterator();
						if (locationsIterator.hasNext()) {// get just the first if any. maxResults=1 didn't work
							JsonObject location = (JsonObject) locationsIterator.next().getAsJsonObject();
							Address addr = addressFromLocationJSON(location);
							JsonObject latLng = location.getAsJsonObject("latLng");
							if (latLng != null) {
								String lat = latLng.get("lat").getAsString();
								String lon = latLng.get("lng").getAsString();
								SpatialAddress spAddress = new SpatialAddress(addr.city, addr.postalCode, addr.streetName,addr.streetNumber, lon, lat);
								// System.out.println("Updated record for address: "
								// + sAddress);
								spAddresses.add(spAddress);
							} else {
								System.err.println("No lon/lat results");
							}
						}
					}
				}

				// TODO: check for equivalence the provided batch records to correlate results with requests. check for geocode failures
				// and log for further refinement
			} catch (Exception e) {
				e.printStackTrace();
			}
			return spAddresses;
		}
	}
	
	Address addressFromProvidedLocationJSON(JsonObject location){
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
	
	Address addressFromLocationJSON(JsonObject location){
		String city = null;
		JsonElement el = null;
		if((el=location.get("adminArea5"))!=null){
			city = el.getAsString();
			el=null;
		}
		int postalCode = 0;
		if((el=location.get("postalCode"))!=null){
			if(el.getAsString().length()>0)
				postalCode = el.getAsInt();
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
		}		
		return new Address(city, postalCode,streetName, streetNumber);
	}
	
	String getUrl(String locationsStr) throws UnsupportedEncodingException{
		String locations = locationsStr;
		if(locationsStr.lastIndexOf(',')>-1)
			locations = locationsStr.substring(0,locationsStr.lastIndexOf(','));
		locations = "&json=" + URLEncoder.encode("{\"locations\":[" + locationsStr + "]}", StandardCharsets.UTF_8.name());
		return serviceUrl.toString() + "/batch?key=" + this.apiKey + "&maxResults=1" + locations;
	}

}
