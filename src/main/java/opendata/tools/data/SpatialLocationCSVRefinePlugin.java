package opendata.tools.data;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import opendata.tools.spatial.GeocodingServiceDelegate;
import opendata.tools.spatial.MapquestGeocodingService;
import opendata.tools.spatial.SpatialAddress;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

public class SpatialLocationCSVRefinePlugin implements CSVRefinePlugin {

	int batchCount = 0;
	int batchSize = 0;
	GeocodingServiceDelegate geocoding = new MapquestGeocodingService("http://www.mapquestapi.com/geocoding/v1", "WeFXGPBnAOeBCtMIowFMcDF5PuvBaeoO");
	
	public SpatialLocationCSVRefinePlugin(int batchSize) {
		this.batchSize = batchSize;
	}

	@Override
	public void doRefine(String cellValue, Map record, List<List> refinedRecords) throws CSVRefineException {
		if(this.batchCount<this.batchSize){
			this.batchCount++;
		} else {
			List<List> recordsToGeolocate = new ArrayList<List>(this.batchSize);
			recordsToGeolocate.add(new ArrayList(record.values()));
			List<Address> addressesToGeolocate = new ArrayList<Address>(this.batchSize);
			addressesToGeolocate.add((Address)record.get(record.size()-1));
			for (ListIterator iterator = refinedRecords.listIterator(refinedRecords.size()); iterator.hasPrevious() && this.batchCount>=0;) {
				List prevRecord = (List) iterator.previous();
				recordsToGeolocate.add(prevRecord);
				addressesToGeolocate.add((Address)prevRecord.get(prevRecord.size()-1));
				this.batchCount--;
			}
			this.geocoding.geocodeBatch(addressesToGeolocate)
			.done(new DoneCallback<List<SpatialAddress>>() {
				@Override
				public void onDone(List<SpatialAddress> spAddress) {
					
				}
			})
			.fail(new FailCallback<Throwable>() {
				@Override
				public void onFail(Throwable result) {
					result.printStackTrace();
				}
			});
		}
	}

}
