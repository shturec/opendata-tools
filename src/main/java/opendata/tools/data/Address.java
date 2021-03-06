package opendata.tools.data;

public class Address {
		
	private static AddressParser parser; 
	static {
		try {
			parser = new BasicAddressParser(new BasicAddressValidator(), false);
		} catch (Exception e) {
			throw new RuntimeException(e);//Parser could not be initialized. Cannot proceed with any validations at all.
		}
	}
	
	private int postalCode;
	private String city;
	private String streetNumber;
	private String streetName;
		
	public Address(){}
	
	public Address(String city, int postCode, String streetName, String streetNumber){
		this.city = city;
		this.postalCode = postCode;
		this.streetName = streetName;
		this.streetNumber = streetNumber;
	}

	public int getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
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
		if(obj==null)
			return false;
		if (getClass() != obj.getClass()) {
	        return false;
	    }
		final Address other = (Address)obj;
		if((this.city==null)? (other.city!=null): !this.city.equals(other.city))
			return false;
		if(this.postalCode!=other.postalCode)
			return false;
		if((this.streetName==null)? (other.streetName!=null): !this.streetName.equals(other.streetName))
			return false;
		if((this.streetNumber==null)? (other.streetNumber!=null): !this.streetNumber.equals(other.streetNumber))
			return false;		
		return true;
	}

	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 53 * hash + (this.city != null ? this.city.hashCode() : 0);
	    hash = 53 * hash + (this.streetName != null ? this.streetName.hashCode() : 0);
	    hash = 53 * hash + (this.streetNumber != null ? this.streetNumber.hashCode() : 0);
	    hash = 53 * hash + this.postalCode;
	    return hash;
	}
}
