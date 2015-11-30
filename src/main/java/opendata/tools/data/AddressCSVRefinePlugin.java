package opendata.tools.data;

import java.util.List;
import java.util.Map;

public class AddressCSVRefinePlugin implements CSVRefinePlugin {

	@Override
	public void doRefine(String cellValue, Map record, List<List> refinedRecords) throws CSVRefineException {
		try {
			Address address = Address.parseString(cellValue);
			record.put(record.keySet().size(), address);
		} catch (AddressParseException e) {
			throw new CSVRefineException(e);
		}
	}

}
