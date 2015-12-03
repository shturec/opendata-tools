package opendata.tools.data.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

public class CsvTransformation {

	Map<Integer, List<CSVRefinePlugin> > transformations;
	List<CSVRefinePlugin> postTransformations;
	CSVProcessor processor = new CSVProcessor();
	
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
			processor.getFormat().print(new OutputStreamWriter(out)).printRecords(csv);
			//IOUtils.writeLines(csv, "\r\n", out);
			//TODO or do it manually line by line and report progress too
			defered.resolve(new Info());//TODO
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
		List<String> warnings;
		List<String> errors;
		List<String> infos;
	}

}
