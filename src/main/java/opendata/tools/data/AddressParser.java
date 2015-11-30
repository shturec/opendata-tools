package opendata.tools.data;


public interface AddressParser {
	
	Address parse(String addressStr) throws AddressParseException;
	
}