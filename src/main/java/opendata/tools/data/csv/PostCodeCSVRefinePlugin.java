package opendata.tools.data.csv;

import java.util.List;
import java.util.Map;

public class PostCodeCSVRefinePlugin implements CSVRefinePlugin {

	public PostCodeCSVRefinePlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doRefine(String cellValue, int cellIndex, Map context, List<List> refinedRecords, List<String> header)throws CSVRefineException {
		if(!cellValue.matches("\b[1-9][0-9]{3}\b*/")){
			String[] tokens = cellValue.split(",");
			for (String token : tokens) {
				context.put(context.keySet().size(), token);
			}
		}
	}

}
