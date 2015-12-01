package opendata.tools.spatial.mapquest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import opendata.tools.data.Address;
import opendata.tools.data.AddressValidator.Validation;
import opendata.tools.data.BasicAddressValidator;
import opendata.tools.http.AsyncHttpClient;
import opendata.tools.spatial.GeocodingServiceDelegate;
import opendata.tools.spatial.SpatialAddress;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapquestGeocodingService implements GeocodingServiceDelegate{

	private static final Logger LOG = LoggerFactory.getLogger(MapquestGeocodingService.class);
	
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
		try {
			String url = this.getUrl(addresses);
			return this.sendAsyncSvcRequest(url);
		} catch (UnsupportedEncodingException e) {
			LOG.warn(e.getMessage(),e);
		}
		return null;
	}
	
	public Promise<List<SpatialAddress>,Throwable,Float> geocode(Address address) {
		RequestLocation loc = new RequestLocation(); 
		String location = loc.toJSONString(address);
		try {
			location = "&json={\"location\":" + URLEncoder.encode(location, StandardCharsets.UTF_8.name());
			String url = serviceUrl.toString() + "/address?key=" + this.apiKey + "&maxResults=1" + location;
			return this.sendAsyncSvcRequest(url);
		} catch (UnsupportedEncodingException e) {
			LOG.warn(e.getMessage(),e);
		}
		return null;//TODO
	}
	
	private Promise<List<SpatialAddress>,Throwable,Float> sendAsyncSvcRequest(String url){
		final DeferredObject<List<SpatialAddress>,Throwable,Float> defered = new DeferredObject<List<SpatialAddress>, Throwable, Float>();
		AsyncHttpClient client = new AsyncHttpClient();
		try {
			client.get(url, new AsyncHttpClient.Callback<String>() {

				@Override
				public void done(String entity) {
					GeocodeResponse response = new GeocodeResponse(entity);
					if(response.getInfo().getStatuscode()>0){
						String msgs = "";
						for (String message : response.getInfo().getMessages()) {
							msgs+=message+";";
						}
						defered.reject(new Exception("Geocode failed. Status code: " + response.getInfo().getStatuscode() + ", messages:"+msgs));	
					} else {
						List<SpatialAddress> spAddress = response.getResults();
						defered.resolve(spAddress);	
					}
				}

				@Override
				public void error(Throwable t) {
					defered.reject(t);
				}
			});				
			return defered.promise();
		} catch (IOException e) {
			LOG.warn(e.getMessage(),e);//log and continue
		}
		return null;
	}

	String getUrl(List<Address> addresses) throws UnsupportedEncodingException{
		GeocodeRequest req = new GeocodeRequest(addresses);
		String locations = "&json=" + URLEncoder.encode(req.getLocations(), StandardCharsets.UTF_8.name());
		return serviceUrl.toString() + "/batch?key=" + this.apiKey + "&maxResults=1" + locations;
	}

}
