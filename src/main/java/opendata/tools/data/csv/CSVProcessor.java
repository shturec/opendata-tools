package opendata.tools.data.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CSVProcessor {

	private CSVFormat format;
	
	public CSVProcessor() {
		this.format = CSVFormat.DEFAULT.withHeader();
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
	 * A reduce-like algorithm for processing CSV records.
	 * 
	 * @param csvResourceStream
	 * @param plugins
	 * @return
	 * @throws IOException
	 * @throws CSVRefineException
	 */
	public List<List> process(InputStream csvResourceStream, Map<Integer, CSVRefinePlugin> plugins) throws IOException, CSVRefineException {
		
		Reader in = new InputStreamReader(csvResourceStream, StandardCharsets.UTF_8);
		Iterable<CSVRecord> records = this.format.parse(in);
		
		List<List> normalizedRecords = new ArrayList<List>();
		List<String> header = null;//headers may by changed by the plugins
		int i=0;
		for (CSVRecord record : records) {
			
			if(!this.format.getSkipHeaderRecord() && i==0){
				header = new ArrayList<String>();
				for (String columnHeader : record) {
					header.add(columnHeader);
				}
				continue;
			}
			i++;
				
			Map ctx = new TreeMap();//copy row in a modifiable data structure sorted by key to preserve the correct index.
			for (int j = 0; j < record.size(); j++) {
				ctx.put(j, record.get(j));
			}
			for (int j = 0; j < record.size(); j++) {
				if(plugins!=null){
					if(plugins.containsKey(j)){
						plugins.get(j).doRefine(record.get(j), j, ctx, normalizedRecords, header);
					} else if(j == record.size()-1 && plugins.containsKey(1000)) {
						plugins.get(1000).doRefine(record.get(j), j, ctx, normalizedRecords, header);	
					}
				}
			}
			normalizedRecords.add(new ArrayList(ctx.values()));
		}
		
		if(header!=null)
			normalizedRecords.add(0, header);
		
		return normalizedRecords;
	}

}
