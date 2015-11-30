package opendata.tools.data;

import java.util.HashMap;
import java.util.Map;

public interface AddressValidator {
	
	 Validation validate(Address address);
	 
	 boolean isValid(Address address);
	 
	 class Validation {
		 boolean hasErrors;
		 //key: address component (= Address class property member name); value: validation error message
		 Map<String, String> errors = new HashMap<String, String>(3); 
	 }
}
