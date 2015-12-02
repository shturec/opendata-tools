package opendata.tools.data.csv;

import java.util.Iterator;
import java.util.List;

import opendata.tools.data.Address;
import opendata.tools.spatial.SpatialAddress;

public class CSVFormat {

	@SuppressWarnings("unchecked")
	public String format(List<List> records, List<String> header){
		if(records.size()>0 && records.get(0).size()>0 && records.get(0).size()+1!=header.size())
			throw new IllegalArgumentException("headers count and record values count dont' match");
		//TODO normalize and replace address segments too. Currently, we shall simply add spatial data
		String content = "";
		for (String head : header) {
			content+=head+",";
		}
		content = content.substring(0,content.lastIndexOf(","));
		content+="\n";
		
		for (List record : records) {
			//Flatten (Spatial)Address object
			Address a = (Address)record.remove(5);
			String lat = "";
			String lon = "";
			if(a instanceof SpatialAddress){
				SpatialAddress addr = (SpatialAddress)a;
				lat = addr.getLatitude();
				lon = addr.getLongitude();
			} 
			record.add(lat);
			record.add(lon);
			for (Iterator iterator = record.iterator(); iterator.hasNext();) {
				content  += iterator.next() + ",";				
			}
			content = content.substring(0,content.lastIndexOf(","));
			content+="\n";
			
		}
		
		return content;
	}
	
}
