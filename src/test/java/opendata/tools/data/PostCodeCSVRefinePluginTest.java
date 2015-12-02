package opendata.tools.data;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opendata.tools.data.csv.CSVRefineException;
import opendata.tools.data.csv.CSVRefinePlugin;
import opendata.tools.data.csv.NormalizedCSV;
import opendata.tools.data.csv.PostCodeCSVRefinePlugin;

import org.junit.Test;

public class PostCodeCSVRefinePluginTest {

	@Test
	public void testDoRefine() {
		PostCodeCSVRefinePlugin p = new PostCodeCSVRefinePlugin();
		Map<Integer, CSVRefinePlugin> plugins = new HashMap<Integer, CSVRefinePlugin>();
		plugins.put(0, p);
		NormalizedCSV r = new NormalizedCSV();
		
		List<List> records;
		try {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("Poshtenski-kodove-na-Bulgaria.csv");
			records = r.normalizeCSV(in, plugins);
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
