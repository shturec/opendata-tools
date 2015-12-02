package opendata.tools.data.csv;

import java.util.List;
import java.util.Map;

import opendata.tools.data.Address;
import opendata.tools.data.AddressParseException;

public class AddressCSVRefinePlugin implements CSVRefinePlugin {

	@Override
	public void doRefine(String cellValue, int cellIndex, Map record, List<List> refinedRecords) throws CSVRefineException {
		try {
			Address address = Address.parseString(cellValue);
			record.put(record.keySet().size(), address);
		} catch (AddressParseException e) {
			throw new CSVRefineException(e);
		}
	}

}
