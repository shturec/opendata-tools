package opendata.tools.data.csv;

import java.util.List;
import java.util.Map;

public interface CSVRefinePlugin {
	
	void doRefine(String cellValue, int cellIndex,  Map record, List<List> refinedRecords, List<String> header) throws CSVRefineException;

}
