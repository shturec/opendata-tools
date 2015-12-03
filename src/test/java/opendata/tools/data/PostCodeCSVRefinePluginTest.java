package opendata.tools.data;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opendata.tools.data.csv.CSVProcessor;
import opendata.tools.data.csv.CSVRefineException;
import opendata.tools.data.csv.CSVRefinePlugin;
import opendata.tools.data.csv.PostCodeCSVRefinePlugin;

import org.junit.Test;

public class PostCodeCSVRefinePluginTest {

	@Test
	public void testDoRefine() {
		PostCodeCSVRefinePlugin p = new PostCodeCSVRefinePlugin();
		Map<Integer, List<CSVRefinePlugin>> plugins = new HashMap<Integer, List<CSVRefinePlugin>>();
		List<CSVRefinePlugin> pluginsSet = new ArrayList<CSVRefinePlugin>();
		pluginsSet.add(p);
		plugins.put(0, pluginsSet);
		CSVProcessor r = new CSVProcessor();
		
		List<List> records;
		try {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("Poshtenski-kodove-na-Bulgaria.csv");
			records = r.process(in, plugins,null);
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
