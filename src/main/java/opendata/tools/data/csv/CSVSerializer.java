package opendata.tools.data.csv;

import java.util.List;
import java.util.Map;

public interface CSVSerializer<T> {
	
	public Map<String, List<String>> serialize(T type, List<String> header); 

}
