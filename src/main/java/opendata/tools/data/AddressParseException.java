package opendata.tools.data;

import java.util.Map;

@SuppressWarnings("serial")
public class AddressParseException extends Exception {

	Map<String, String> validaitonErrors; 
	
	public AddressParseException(Map<String, String> errors) {
		super(toErrorMessage(errors));
		this.validaitonErrors = errors;
	}
	
	static String toErrorMessage(Map<String, String> errors){		
		return errors.toString();
	}
	
	public Map<String, String> getValidationErrors(){
		return this.validaitonErrors;
	}

}
