package opendata.tools.data.csv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import opendata.tools.data.Address;
import opendata.tools.spatial.GeocodingServiceDelegate;
import opendata.tools.spatial.SpatialAddress;
import opendata.tools.spatial.mapquest.MapquestGeocodingService;

import org.apache.commons.io.IOUtils;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class SpatialLocationCSVRefinePlugin implements CSVRefinePlugin {

	int batchCount = 0;
	int batchSize = 0;

	GeocodingServiceDelegate geocoding;
	
	public SpatialLocationCSVRefinePlugin(String url, String apiKey, int batchSize) throws JsonSyntaxException, IOException {
		this.batchSize = batchSize;
		JsonParser jparse = new JsonParser();
		JsonObject cfgJson = (JsonObject) jparse.parse(IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("cfg.json"), StandardCharsets.UTF_8));
		JsonObject geocodeSvcJson = cfgJson.get("geocoding-service").getAsJsonObject();
		String geocodeSvcUrl = geocodeSvcJson.get("api").getAsString();
		String geocodeSvcApiKey = geocodeSvcJson.get("api-key").getAsString();
		geocoding = new MapquestGeocodingService(geocodeSvcUrl, geocodeSvcApiKey);
	}

	@Override
	public void doPostRefine(List outputRecord, List<List> outputRecords, List<String> header) throws CSVRefineException{
	//public void doRefine(String cellValue, int cellIndex, Map record, List<List> refinedRecords, List<String> header) throws CSVRefineException {
		if(this.batchCount<this.batchSize){
			//iterate and let other plugins do their job until the batch size limit is reached
			this.batchCount++;
		} else {
			//now look back for the length of the batch size and get a set of records to geocode. It is expected that they contain already instances of Address.
			final List<List> recordsToGeolocate = new ArrayList<List>(this.batchSize);
			recordsToGeolocate.add(new ArrayList(outputRecord));
			List<Address> addressesToGeolocate = new ArrayList<Address>(this.batchSize);
			addressesToGeolocate.add((Address)outputRecord.get(outputRecord.size()-1));
			for (ListIterator iterator = outputRecords.listIterator(outputRecords.size()); iterator.hasPrevious() && this.batchCount>=0;) {
				List prevRecord = (List) iterator.previous();
				recordsToGeolocate.add(prevRecord);
				Object pRec = prevRecord.get(prevRecord.size()-1);
				if(!(pRec instanceof Address)){
					throw new CSVRefineException("The record " + prevRecord + " does not contain a Address object to Geocode.");
				}
				addressesToGeolocate.add((Address)prevRecord.get(prevRecord.size()-1));
				this.batchCount--;
			}
			//TODO: refactor to async again
			try {
				this.geocoding.geocodeBatch(addressesToGeolocate)
				.done(new DoneCallback<List<SpatialAddress>>() {
					@Override
					public void onDone(List<SpatialAddress> spAddresses) {
						int i = 0;
						for (List record : recordsToGeolocate) {
							record.remove(record.size()-1);
							record.add(spAddresses.get(i));
							i++;
						}
					}
				})
				.fail(new FailCallback<Throwable>() {
					@Override
					public void onFail(Throwable result) {
						result.printStackTrace();
					}
				}).waitSafely(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void doRefine(String cellValue, int cellIndex, List outputRecord, List<List> outputRecords, List<String> header) throws CSVRefineException {
		
	}

}
