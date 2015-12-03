package opendata.tools.data.csv;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opendata.tools.data.csv.CsvTransformation.Info;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise.State;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class CsvTransformationTest {

	private static String geocodeSvcUrl = "";
	private static String geocodeSvcApiKey = "";
	
	@BeforeClass
	public static void setup() throws JsonSyntaxException, IOException{
		JsonParser jparse = new JsonParser();
		JsonObject cfgJson = (JsonObject) jparse.parse(IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("cfg.json"), StandardCharsets.UTF_8));
		JsonObject geocodeSvcJson = cfgJson.get("geocoding-service").getAsJsonObject();
		geocodeSvcUrl = geocodeSvcJson.get("api").getAsString();
		geocodeSvcApiKey = geocodeSvcJson.get("api-key").getAsString();
	}
	
	@Test
	public void testCsvEtlJob() {
		//fail("Not yet implemented");
	}

	@Test
	public void testEtl() {
		CsvTransformation etl = new CsvTransformation(null, null);
		try {
			final InputStream in = getFileIn();
			final OutputStream out = getFileOut();
			etl.etl(in, out)
			.fail(fc)
			.done(dc)
			.always(new AlwaysCallback<CsvTransformation.Info, Throwable>() {
				@Override
				public void onAlways(State state, Info resolved, Throwable rejected) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEtl1() throws JsonSyntaxException, IOException {
		Map<Integer, List<CSVRefinePlugin>> plugins = new HashMap<Integer, List<CSVRefinePlugin>>();
		List<CSVRefinePlugin> porcessPlugins = new ArrayList<CSVRefinePlugin>();
		AddressCSVRefinePlugin ap = new AddressCSVRefinePlugin();
		porcessPlugins.add(ap);
		plugins.put(1, porcessPlugins);
		
		List<CSVRefinePlugin> postPorcessPlugins = new ArrayList<CSVRefinePlugin>();
		SpatialLocationCSVRefinePlugin p = new SpatialLocationCSVRefinePlugin(geocodeSvcUrl, geocodeSvcApiKey, 5);
		postPorcessPlugins.add(p);
	
		CsvTransformation etl = new CsvTransformation(plugins, postPorcessPlugins);
		try {
			final InputStream in = getFileIn();
			final OutputStream out = getFileOut();
			etl.etl(in, out)
			.fail(fc)
			.done(dc)
			.always(new AlwaysCallback<CsvTransformation.Info, Throwable>() {
				@Override
				public void onAlways(State state, Info resolved, Throwable rejected) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
        /*try {
			Thread.currentThread().sleep(10000);
		} catch (InterruptedException e) {}*/
	}
	
	FailCallback<Throwable> fc = new FailCallback<Throwable>() {	
		@Override
		public void onFail(Throwable result) {
			result.printStackTrace();
			fail(result.getMessage());
		}
	};
	
	DoneCallback<Info> dc = new DoneCallback<Info>() {
		@Override
		public void onDone(Info info) {
			//TODO: check
		}
	};

	private InputStream getFileIn() throws IOException{
		List<String> lines = FileUtils.readLines(new File("C:/dev/git/opendata-tools/src/main/resources/data.csv")).subList(0, 20);
		String s = "";
		for (String line : lines) {
			s+=line+"\r\n";
		}
		return new BufferedInputStream(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
	}
	
	private OutputStream getFileOut() throws FileNotFoundException{
		FileOutputStream fos = new FileOutputStream("data1.csv");
		return new BufferedOutputStream(fos);
	}
	
}
