package opendata.tools.spatial;

import opendata.tools.data.Address;

public class SpatialAddress extends Address {

	String lon;
	String lat;

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
		if(super.equals(obj)){
			if(obj instanceof SpatialAddress){
				SpatialAddress sOther = (SpatialAddress)obj;
				if(((this.lat==null && sOther.lat==null) ||  this.lat.equals(sOther.lat)) && ((this.lon==null && sOther.lon==null) ||  this.lon.equals(sOther.lon))){
					return true;
				}
			}
		}
		return false;
	}
}
