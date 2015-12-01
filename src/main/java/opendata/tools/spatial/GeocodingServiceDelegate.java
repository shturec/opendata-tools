package opendata.tools.spatial;

import java.util.List;

import opendata.tools.data.Address;
import opendata.tools.data.AddressValidator;

import org.jdeferred.Promise;

public interface GeocodingServiceDelegate {

	AddressValidator.Validation validateAddress(Address address);
	
	Promise<List<SpatialAddress>,Throwable,Float> geocode(Address address);
	
	Promise<List<SpatialAddress>,Throwable,Float> geocodeBatch(List<Address> addresses);
		
}
