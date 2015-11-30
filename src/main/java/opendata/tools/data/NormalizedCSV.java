package opendata.tools.data;

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

public class NormalizedCSV {
	
	List<List> normalizeCSV(Iterable<CSVRecord> records, int skipRows, Map<Integer, CSVRefinePlugin> plugins) throws IOException, CSVRefineException{
		List<List> normalizedRecords = new ArrayList<List>();
		int i=0;
		for (CSVRecord record : records) {
			if(skipRows<1 || i>=skipRows){
				
				Map ctx = new TreeMap();
				for (int j = 0; j < record.size(); j++) {
					ctx.put(j, record.get(j));
				}
				for (int j = 0; j < record.size(); j++) {
					if(plugins!=null){
						if(plugins.containsKey(j)){
							plugins.get(j).doRefine(record.get(j), ctx, normalizedRecords);
						}
						if(j == record.size()-1 && plugins.containsKey(1000)){
							plugins.get(1000).doRefine(record.get(j), ctx, normalizedRecords);
						}
					}
				}
				normalizedRecords.add(new ArrayList(ctx.values()));
			}
			i++;
		}
		return normalizedRecords;
	}
	
	public Iterable<CSVRecord> load(String resourceName) throws IOException{
		InputStream dataIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
		Reader in = new InputStreamReader(dataIn, StandardCharsets.UTF_8);
		return CSVFormat.DEFAULT.parse(in);				
	}

}
