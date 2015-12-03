package opendata.tools.data.csv;

import java.util.List;

public interface CSVRefinePlugin {
	
	/**
	 * 
	 * @param cellValue
	 * @param cellIndex
	 * @param outputRecord
	 * @param outputRecords
	 * @param header
	 * @throws CSVRefineException
	 */
	void doRefine(String cellValue, int cellIndex,  List outputRecord, List<List> outputRecords, List<String> header) throws CSVRefineException;

	//TODO move in own interface distinct by invocation phase or invoke concrete method per phase 
	void doPostRefine(Object cellValue, int cellIndex,  List outputRecord, List<List> outputRecords, List<String> header) throws CSVRefineException;

}
