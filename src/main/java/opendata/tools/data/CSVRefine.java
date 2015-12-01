package opendata.tools.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opendata.tools.spatial.SpatialAddress;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVRefine {
	
	private static final Logger LOG = LoggerFactory.getLogger(CSVRefine.class);

	public CSVRefine() {}
	
	public void refine(File sourceCsv, File refinedCsv) throws IOException, CSVRefineException{
		
		//TODO load header form the CSV file and update
		Map<Integer, CSVRefinePlugin> plugins = new HashMap<Integer, CSVRefinePlugin>();
		plugins.put(1, new AddressCSVRefinePlugin());
		plugins.put(1000, new SpatialLocationCSVRefinePlugin(5));
		NormalizedCSV r = new NormalizedCSV();

		Iterable<CSVRecord> records = r.load(sourceCsv.getName());
		List<List> normalizedRecords = r.normalizeCSV(records, 1, plugins);
		
		//stats
		int i = 0;
		int j = 0;
		for (List record : normalizedRecords) {
			if(record.get(record.size()-1)!=null){
				Address a = (Address)record.get(record.size()-1);			
				if(a.getCity()==null || a.getPostalCode()<1000 || a.getStreetName()==null || a.getStreetNumber()==null){
					j++;
				}
				if(!(a instanceof SpatialAddress))
					LOG.warn("Could not geocode: "+ record);
			}
			i++;
		}
		System.out.println("incomplete: " + j + " of " + i +" in total");
		
		opendata.tools.data.CSVFormat format = new opendata.tools.data.CSVFormat();
		String refinedCSV = format.format(normalizedRecords, Arrays.asList(new String[]{"№","Адрес","Име на училище","Вид училище по чл. 10 от ЗНП","Вид училище по чл. 26от ЗНП","lat","lon"}));

		File f = new File(refinedCsv.getPath());//todo: new file intead of overwrite - another parameter
		FileUtils.writeStringToFile(f, refinedCSV, StandardCharsets.UTF_8.name(), false);
	}
		
	public static void main(String[] args) throws IOException, CSVRefineException {
		CSVRefine refine = new CSVRefine();
		refine.refine(new File("data.csv"), new File("data-refined.csv"));
	}
	
}
