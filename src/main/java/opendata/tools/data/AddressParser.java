package opendata.tools.data;


public interface AddressParser {
	
	/**
	 * Generate a Java object model form an address encoded in string. 
	 * @param addressStr
	 * @return
	 * @throws AddressParseException
	 */
	Address parse(String addressStr) throws AddressParseException;

}