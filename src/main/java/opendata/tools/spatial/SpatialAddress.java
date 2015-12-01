package opendata.tools.spatial;

import opendata.tools.data.Address;

public class SpatialAddress extends Address {

	private String lon;
	private String lat;

	public SpatialAddress() {}

	public SpatialAddress(String city, int postCode, String streetName, String streetNumber, String lon, String lat) {
		super(city, postCode, streetName, streetNumber);
		this.lon = lon;
		this.lat = lat;
	}
	
	public void setLongitude(String lon){
		this.lon = lon;
	}
	
	public String getLongitude(){
		return this.lon;
	}
	
	public void setLatitude(String lat){
		this.lat = lat;
	}
	
	public String getLatitude(){
		return this.lat;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", [lat:"+ this.lat+",lon:"+this.lon+"]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj ==null)
			return false;
		if (getClass() != obj.getClass()) {
	        return false;
	    }
		final SpatialAddress other = (SpatialAddress)obj;
		if((this.lon==null)? (other.lon!=null): !this.lon.equals(other.lon))
			return false;
		if((this.lat==null)? (other.lat!=null): !this.lat.equals(other.lat))
			return false;
		return false;
	}
	
	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 53 * super.hashCode();
	    hash = 53 * hash + (this.lon != null ? this.lon.hashCode() : 0);
	    hash = 53 * hash + (this.lat != null ? this.lat.hashCode() : 0);
	    return hash;
	}
}
