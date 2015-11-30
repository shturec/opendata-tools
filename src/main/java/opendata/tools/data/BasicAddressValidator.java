package opendata.tools.data;

public class BasicAddressValidator implements AddressValidator {

	@Override
	public Validation validate(Address address) {
		Validation v = new Validation();
		if(address.city == null){
			v.errors.put("city", "Address.city is null");
		} else if(address.city.length()<1){
			v.errors.put("city", "Address.city string length < 1");
		}
		if(address.streetName == null){
			v.errors.put("streetName", "Address.streetName is null");
		} else if(address.streetName.length()<1){
			v.errors.put("streetName", "Address.streetName string length < 1");
		}
		if(address.streetNumber == null){
			v.errors.put("streetNumber", "Address.streetNumber is null");
		} else if(address.streetNumber.length()<1){
			v.errors.put("streetNumber", "Address.streetNumber string length < 1");
		}
		if(!v.errors.isEmpty())
			v.hasErrors = true;
		return v;
	}

	@Override
	public boolean isValid(Address address) {
		return this.validate(address).hasErrors;
	}

}
