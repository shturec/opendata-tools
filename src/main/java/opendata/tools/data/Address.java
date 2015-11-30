package opendata.tools.data;

import java.io.IOException;

public class Address {
		
	static AddressParser parser; 
	static {
		try {
			parser = new BasicAddressParser(new BasicAddressValidator(), false);
		} catch (IOException | CSVRefineException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int postalCode;
	public String city;
	public String streetNumber;
	public String streetName;
	
	public Address(){}
	
	public Address(String city, int postCode, String streetName, String streetNumber){
		this.city = city;
		this.postalCode = postCode;
		this.streetName = streetName;
		this.streetNumber = streetNumber;
	}

	public static Address parseString(String addressStr) throws AddressParseException{
		return parseString(addressStr, parser);
	}
	
	public static Address parseString(String addressStr, AddressParser parser) throws AddressParseException{
		return parser.parse(addressStr);
	}
	
	@Override
	public String toString() {
		return this.city + " " + this.postalCode + ", " + this.streetNumber + " " + this.streetName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj)
			return true;
		if(obj instanceof Address){
			Address other = (Address)obj;
			if(((this.city==null && other.city==null) || this.city.equals(other.city)) && 
				this.postalCode==other.postalCode &&
				((this.streetName==null && other.streetName==null) || this.streetName.equals(other.streetName)) &&
				((this.streetNumber==null && other.streetNumber==null) || this.streetNumber.equals(other.streetNumber))){
				return true;
			}
		} 
		return false;
	}
}
