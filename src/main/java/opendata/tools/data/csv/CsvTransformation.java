package opendata.tools.data.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import opendata.tools.data.Address;
import opendata.tools.spatial.SpatialAddress;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

public class CsvTransformation {

	Map<Integer, List<CSVRefinePlugin> > transformations;
	List<CSVRefinePlugin> postTransformations;
	CSVProcessor processor = new CSVProcessor();
	AddressCSVRefinePlugin p = new AddressCSVRefinePlugin();
	
	public CsvTransformation(Map<Integer, List<CSVRefinePlugin>> recordTransforamtions, List<CSVRefinePlugin> recordPostTransformations) {
		this.transformations = recordTransforamtions;
		this.postTransformations = recordPostTransformations;
	}
	
	/**
	 * This can take a while...
	 * @param csvIn
	 * @param out
	 * @return
	 */
	public Promise<Info, Throwable, Float> etl(InputStream csvIn, OutputStream out){
		final DeferredObject<Info, Throwable, Float> defered = new DeferredObject<Info, Throwable, Float>();

		try {
			List<List> csv = processor.process(csvIn, this.transformations, this.postTransformations);
			int i=0;
			List<String> header = null;
			Info info = new Info();
			int countsuccess = 0;
			for (List record : csv) {
				if(i>0){
					List<String> values = null;
					Address spAddress = null;
					for (Object value : record) {
						if(value instanceof SpatialAddress){
							spAddress = (SpatialAddress)value;
							Map<String, List<String>> output = p.serialize(spAddress);
							values = output.get("records");
							if(header.indexOf("Address")>-1){
								header.remove("Address");
								header.addAll(output.get("header"));
							}
						} else if(value instanceof Address){
							spAddress = (Address)value;
							info.warnings.add("Address could not be geocoded: "+ value);
						}
					}
					if(spAddress!=null){
						record.remove(spAddress);
					} else {
						if(values==null){
							values = new ArrayList(Arrays.asList(new String[]{"",""}));
						} 
						info.warnings.add("No Address or SpatialAddress found on record "+ i);
					}
					if(values!=null){
						record.addAll(values);
						if(values.get(0).length()>0){
							countsuccess++;							
						}
					} 
				} else {
					header = record;					
				}
				i++;
			}
			info.infos.add("Total number of successfull transformations: "+countsuccess);
//			processor.getFormat().print(new OutputStreamWriter(out)).printRecords(csv);
			processor.getFormat().print(System.out).printRecords(csv);
			//IOUtils.writeLines(csv, "\r\n", out);
			//TODO or do it manually line by line and report progress too
			defered.resolve(info);
		} catch (IOException | CSVRefineException e) {
			e.printStackTrace();
			defered.reject(e);
		}
		return defered.promise();
	}

	public CSVProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(CSVProcessor processor) {
		this.processor = processor;
	}
	
	public class Info {
		List<String> warnings = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();;
		List<String> infos = new ArrayList<String>();;
	}

}
