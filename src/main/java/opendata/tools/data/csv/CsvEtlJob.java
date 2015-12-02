package opendata.tools.data.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

public class CsvEtlJob {

	Map<Integer, CSVRefinePlugin> transformations;
	
	public CsvEtlJob(Map<Integer, CSVRefinePlugin> transformations) {
		this.transformations = transformations;
	}
	
	/**
	 * This can take a while...
	 * @param csvIn
	 * @param out
	 * @return
	 */
	public Promise<Void, Throwable, Float> etl(InputStream csvIn, OutputStream out){
		final DeferredObject<Void,Throwable,Float> defered = new DeferredObject<Void, Throwable, Float>();
		CSVProcessor processor = new CSVProcessor();
		try {
			List<List> csv = processor.process(csvIn, transformations);
			IOUtils.writeLines(csv, "\n", out);
			//TODO or do it manually line by line and report progress too
			defered.resolve(null);
		} catch (IOException | CSVRefineException e) {
			e.printStackTrace();
			defered.reject(e);
		}
		return defered.promise();
	}

}
