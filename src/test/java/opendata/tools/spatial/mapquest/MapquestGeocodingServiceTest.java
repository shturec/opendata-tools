package opendata.tools.spatial.mapquest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import opendata.tools.data.Address;
import opendata.tools.spatial.GeocodingServiceDelegate;
import opendata.tools.spatial.SpatialAddress;
import opendata.tools.spatial.mapquest.MapquestGeocodingService;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.junit.Before;
import org.junit.Test;

public class MapquestGeocodingServiceTest {

	private static final String API_KEY = "WeFXGPBnAOeBCtMIowFMcDF5PuvBaeoO";
	private static final String SERVICE_URL = "http://www.mapquestapi.com/geocoding/v1";
	
	GeocodingServiceDelegate s;
	
	@Before
	public void setup(){
		this.s = new MapquestGeocodingService(SERVICE_URL, API_KEY);		
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
