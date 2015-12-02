package opendata.tools.data.csv;

import java.util.List;
import java.util.Map;

import opendata.tools.data.Address;
import opendata.tools.data.AddressParseException;
import opendata.tools.spatial.SpatialAddress;

public class AddressCSVRefinePlugin implements CSVRefinePlugin, CSVSerializer<Address> {

	@Override
	public void doRefine(String cellValue, int cellIndex, Map record, List<List> refinedRecords, List<String> header) throws CSVRefineException {
		try {
			Address address = Address.parseString(cellValue);
			record.put(record.keySet().size(), address);
		} catch (AddressParseException e) {
			throw new CSVRefineException(e);
		}
	}

	@Override
	public void serialize(Address value, List ctx, List<String> record, List<String> header) {
		//Flatten (Spatial)Address object
		int position = ctx.indexOf(value);
		String lat = "";
		String lon = "";
		if(value instanceof SpatialAddress){
			SpatialAddress addr = (SpatialAddress)value;
			lat = addr.getLatitude();
			lon = addr.getLongitude();
		}
		record.add(lat);
		if(header.size()<record.size())
			header.add("Lat");//TODO:i18n?
		record.add(lon);
		if(header.size()<record.size())
			header.add("Lon");//TODO:i18n?
		//TODO: we have the source address. decide whether to replace or not. currnet strategy is olny to add spatial data if any.
	}

}
