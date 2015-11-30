package opendata.tools.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class PostCodeCSVRefinePluginTest {

	@Test
	public void testDoRefine() {
		PostCodeCSVRefinePlugin p = new PostCodeCSVRefinePlugin();
		Map<Integer, CSVRefinePlugin> plugins = new HashMap<Integer, CSVRefinePlugin>();
		plugins.put(1, p);
		NormalizedCSV r = new NormalizedCSV();
		
		List<List> records;
		try {
			records = r.normalizeCSV(r.load("Poshtenski-kodove-na-Bulgaria.csv"), 1, plugins);
			assertTrue(records.size()>0);
			for (List record : records) {
				System.out.println(record);
			}
		} catch (IOException | CSVRefineException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}

}
