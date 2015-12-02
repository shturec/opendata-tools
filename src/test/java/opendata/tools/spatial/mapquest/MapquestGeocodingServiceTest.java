package opendata.tools.spatial.mapquest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import opendata.tools.data.Address;
import opendata.tools.spatial.GeocodingServiceDelegate;
import opendata.tools.spatial.SpatialAddress;
import opendata.tools.spatial.mapquest.MapquestGeocodingService;

import org.apache.commons.io.IOUtils;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class MapquestGeocodingServiceTest {

	GeocodingServiceDelegate s;
	private String geocodeSvcUrl = "";
	private String geocodeSvcApiKey = "";
	
	@Before
	public void setup() throws JsonSyntaxException, IOException{
		JsonParser jparse = new JsonParser();
		JsonObject cfgJson = (JsonObject) jparse.parse(IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("cfg.json"), StandardCharsets.UTF_8));
		JsonObject geocodeSvcJson = cfgJson.get("geocoding-service").getAsJsonObject();
		this.geocodeSvcUrl = geocodeSvcJson.get("api").getAsString();
		this.geocodeSvcApiKey = geocodeSvcJson.get("api-key").getAsString();

		this.s = new MapquestGeocodingService(geocodeSvcUrl, geocodeSvcApiKey);		
	}
	
	@Test
	public void testGeocodeBatch() {
		List<Address> addresses = new ArrayList<Address>();
		addresses.add(new Address("София", 1000, "Цар Борис III", "136А"));
		this.s.geocodeBatch(addresses).done(new DoneCallback<List<SpatialAddress>>(){

			@Override
			public void onDone(List<SpatialAddress> spAddresses) {
				assertTrue(spAddresses.size()==1);
				if(spAddresses!=null){
					for (Address address : spAddresses) {
						System.out.println("Success:" + address);	
					}	
				}				
			}
			
		}).fail(new FailCallback<Throwable>() {

			@Override
			public void onFail(Throwable t) {
				t.printStackTrace();
				fail(t.getMessage());
			}
		});
	}

	@Test
	public void testToJSON() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddressFromProvidedLocationJSON() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddressFromLocationJSON() {
		fail("Not yet implemented");
	}

}
