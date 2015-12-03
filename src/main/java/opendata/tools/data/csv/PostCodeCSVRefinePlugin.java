package opendata.tools.data.csv;

import java.util.List;

public class PostCodeCSVRefinePlugin implements CSVRefinePlugin {

	public PostCodeCSVRefinePlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doRefine(String cellValue, int cellIndex, List outputRecord, List<List> outputRecords, List<String> header) throws CSVRefineException {
		if(!cellValue.matches("\b[1-9][0-9]{3}\b*/")){
			String[] tokens = cellValue.split(",");
			for (String token : tokens) {
				outputRecord.add(token);
			}
		}
	}

	@Override
	public void doPostRefine(Object cellValue, int cellIndex, List outputRecord, List<List> outputRecords, List<String> header) throws CSVRefineException {
		//none
	}

}
