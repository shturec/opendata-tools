package opendata.tools.data;

import java.util.List;
import java.util.Map;

public interface CSVRefinePlugin {
	
	void doRefine(String cellValue,  Map record, List<List> refinedRecords) throws CSVRefineException;

}
