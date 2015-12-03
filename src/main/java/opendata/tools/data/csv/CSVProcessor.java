package opendata.tools.data.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import opendata.tools.data.Address;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CSVProcessor {

	private CSVFormat format;
	
	public CSVProcessor() {
		this.format = CSVFormat.DEFAULT;
	}
	
	public CSVFormat getFormat(){
		return format;
	}

	public void setFormat(CSVFormat format){
		this.format = format;
	}
	
	public List<String> getHeader(){
		return null;
	}
	
	/**
	 * Iterates on SCV records invoking configured plugins to process values and records.
	 * 
	 * @param csvResourceStream
	 * @param plugins
	 * @return
	 * @throws IOException
	 * @throws CSVRefineException
	 */
	public List<List> process(InputStream csvResourceStream, Map<Integer, List<CSVRefinePlugin>> plugins, List<CSVRefinePlugin> postProcessStagePlugins) throws IOException, CSVRefineException {
		
		Reader in = new InputStreamReader(csvResourceStream, StandardCharsets.UTF_8);
		Iterable<CSVRecord> records = this.format.parse(in);
		
		List<List> outputRecords = new ArrayList<List>();
		List<String> header = null;//headers may by changed by the plugins
		int i=0;
		for (CSVRecord record : records) {
			
			//Due to a bug .withHeader() reads the first line but does not return anything when getHeader is invoked. Need to do all this manually. 
			if(!this.format.getSkipHeaderRecord() && i==0){
				header = new ArrayList<String>();
				for (String columnHeader : record) {
					header.add(columnHeader);
				}
				i++;
				continue;
			}
			
			List outputRecord = new ArrayList();//deep copy row in a modifiable data structure
			for (String value : record) {
				outputRecord.add(value);
			}
			
			//TODO: add pre-process phase for completeness. May include some sanity checks for example
			
			//iterate through the record values and invoke registered plugins for each position.
			for (int valuePositionInRecord = 0; valuePositionInRecord < record.size(); valuePositionInRecord++) {
				String value = record.get(valuePositionInRecord);
				if(plugins!=null){
					if(plugins.containsKey(valuePositionInRecord)){
						for (CSVRefinePlugin plugin : plugins.get(valuePositionInRecord)) {
							plugin.doRefine(value, valuePositionInRecord, outputRecord, outputRecords, header);							
						}
					}
				}
			}

			/*Post processing stage*/
			if(postProcessStagePlugins!=null){
				for (CSVRefinePlugin postPlugin : postProcessStagePlugins) {
					for (int valuePositionInRecord = 0; valuePositionInRecord < outputRecord.size(); valuePositionInRecord++) {
						Object value = outputRecord.get(valuePositionInRecord);
						if(value instanceof Address)
							postPlugin.doPostRefine(outputRecord, outputRecords, header);
					}
				}
			}
			
			outputRecords.add(outputRecord);
		}
		
		if(header!=null)
			outputRecords.add(0, header);
		
		return outputRecords;
	}

}
