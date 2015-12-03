package opendata.tools.data.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opendata.tools.data.Address;
import opendata.tools.data.AddressParseException;
import opendata.tools.spatial.SpatialAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressCSVRefinePlugin implements CSVRefinePlugin, CSVSerializer<Address> {

	private static final Logger LOG = LoggerFactory.getLogger(AddressCSVRefinePlugin.class);
	
	@Override
	public void doRefine(String cellValue, int cellIndex, List outputRecord, List<List> outputRecords, List<String> header) throws CSVRefineException {
		try {
			long t1 = System.currentTimeMillis();
			Address address = Address.parseString(cellValue);
			LOG.debug("Parsed in " + (System.currentTimeMillis()-t1));
			outputRecord.add(address);
			if(header.size() < outputRecord.size()){
				header.add("Address");
			}				
		} catch (AddressParseException e) {
			throw new CSVRefineException(e);
		}
	}

	@Override
	public Map<String, List<String>> serialize(Address value) {
		//Flatten (Spatial)Address object
		Map<String, List<String>> output = new HashMap<String, List<String>>(4);
		List<String> record = new ArrayList<String>(); 
		String lat = "";
		String lon = "";
		if(value instanceof SpatialAddress){
			SpatialAddress addr = (SpatialAddress)value;
			lat = addr.getLatitude();
			lon = addr.getLongitude();
		}
		record.add(lat);
		List<String> header = new ArrayList<String>();
		if(header.indexOf("Lat")<0)
			header.add("Lat");//TODO:i18n?
		record.add(lon);
		if(header.indexOf("Lon")<0)
			header.add("Lon");//TODO:i18n?
		//TODO: we have the source address. decide whether to replace or not. currnet strategy is olny to add spatial data if any.
		output.put("header", header);
		output.put("records", record);
		return output;
	}
	
	@Override
	public void doPostRefine(List outputRecord, List<List> outputRecords, List<String> header) throws CSVRefineException {
		// TODO Auto-generated method stub		
	}

}
