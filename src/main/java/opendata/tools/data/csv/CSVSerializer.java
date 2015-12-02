package opendata.tools.data.csv;

import java.util.List;

public interface CSVSerializer<T> {
	
	public void serialize(T type, List ctx, List<String> record, List<String> header); 

}
