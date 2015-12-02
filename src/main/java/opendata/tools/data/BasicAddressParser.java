package opendata.tools.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opendata.tools.data.AddressValidator.Validation;
import opendata.tools.data.csv.CSVRefineException;
import opendata.tools.data.csv.CSVRefinePlugin;
import opendata.tools.data.csv.CSVProcessor;
import opendata.tools.data.csv.PostCodeCSVRefinePlugin;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAddressParser implements AddressParser {
	
	private static final Logger LOG = LoggerFactory.getLogger(BasicAddressParser.class);

	Map<String, List<String>> oblIndexByCode;
	Map<String, List<String>> obshtIndexByCode;
	Map<String, List<String>> ekatteIndexByName;
	
	Map<String, List<String>> ekatteRecordsByNasMiasto = new HashMap<String, List<String>>();
	Map<Integer, List<String>> postCodes;
	AddressValidator addressValidator;
	boolean strict;
	
	public BasicAddressParser() throws Exception{
		this(null, false);
	}
	
	public BasicAddressParser(AddressValidator addressValidator, boolean strict) throws Exception {
		this.addressValidator = addressValidator;
		this.strict = strict;
		
		Iterable<CSVRecord> ekoblRecords = this.loadEkResource("Ekobl.csv", CSVFormat.DEFAULT.withDelimiter(';'));
		this.oblIndexByCode = this.indexEkByCode(ekoblRecords);
		
		Iterable<CSVRecord> ekobstRecords = this.loadEkResource("Ekobst.csv", CSVFormat.DEFAULT.withDelimiter(';'));
		this.obshtIndexByCode = this.indexEkByCode(ekobstRecords);
		
		Iterable<CSVRecord> ekatteRecords = this.loadEkResource("Ekatte.csv", CSVFormat.DEFAULT);		
		this.ekatteIndexByName = this.indexEkatteByCode(ekatteRecords);//the name that we look for as index happen to be on the same position as code so we reuse
		denormalizeEkatte(this.ekatteIndexByName, this.oblIndexByCode, this.obshtIndexByCode);
		
		this.loadPCodeResource("Poshtenski-kodove-na-Bulgaria.csv");
		
	}
	
	void loadPCodeResource(String resourceName) throws IOException, CSVRefineException{
		PostCodeCSVRefinePlugin p = new PostCodeCSVRefinePlugin();
		Map<Integer, CSVRefinePlugin> plugins = new HashMap<Integer, CSVRefinePlugin>();
		plugins.put(0, p);
		CSVProcessor r = new CSVProcessor();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
		List<List> records = r.process(in, plugins);
		this.postCodes = new HashMap<Integer, List<String>>(records.size());
		for (List<String> record : records) {
			try{
				this.postCodes.put(Integer.parseInt(record.get(1)), record);
			} catch(NumberFormatException nfe){}
		}
	}
	
	Iterable<CSVRecord> loadEkResource(String resourceName, CSVFormat format) throws IOException{
		InputStream dataIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
		Reader in = new InputStreamReader(dataIn, StandardCharsets.UTF_8);
		return format.parse(in);
	}
	
	Map<String, List<String>> indexEkByCode(Iterable<CSVRecord> records){
		Map<String, List<String>> index = new HashMap<String, List<String>>();
		int i = 0;
		for (CSVRecord record : records) {
			if(i>1){
				List<String> rec = new ArrayList<String>();
				String key = null;
				for (int j = 0; j < record.size(); j++) {
					String val = record.get(j);
					if(j==0)
						key = val;
					rec.add(val);
				}
				index.put(key, rec);
			}
			i++;
		}
		return index;
	}

	Map<String, List<String>> indexEkatteByCode(Iterable<CSVRecord> records){
		Map<String, List<String>> index = new HashMap<String, List<String>>();
		int i = 0;
		for (CSVRecord record : records) {
			if(i>1){
				List<String> rec = new ArrayList<String>();
				String key = null;
				for (int j = 0; j < record.size(); j++) {
					String val = record.get(j);
					if(j==2)
						key = val;
					rec.add(val);
				}
				index.put(key, rec);
			}
			i++;
		}
		return index;
	}
	
	void denormalizeEkatte(Map<String, List<String>> ekatteIndex, Map<String, List<String>> ekOblastiByCode, Map<String, List<String>> ekObshtiniByCode){
		for (Map.Entry<String, List<String>> kv : ekatteIndex.entrySet()) {
			List<String> denormalizedRecord = new ArrayList<String>();
			for (int j = 0; j < kv.getValue().size(); j++) {
				String value = kv.getValue().get(j);
				if(j == 3){
					String oblCode = kv.getValue().get(j);
					if(ekOblastiByCode.containsKey(oblCode)){
						List<String> oblRecord = ekOblastiByCode.get(oblCode);
						value = oblRecord.get(2);
					} else {
						LOG.debug("Unknown code for Oblast: " + oblCode);
					}						
				}
				if(j == 4){
					String obshtCode = kv.getValue().get(j);
					if(ekObshtiniByCode.containsKey(obshtCode)){
						List<String> obshtRecord = ekObshtiniByCode.get(obshtCode);
						value = obshtRecord.get(2);
					} else {
						LOG.debug("Unknown code for Obshtina: " + obshtCode);
					}						
				}
				denormalizedRecord.add(value);
			}
			kv.setValue(denormalizedRecord);
		}
	}
	
	public Address parse(String addressStr) throws AddressParseException{
		String[] addressTokens = addressStr.split(",");

		List<String> postCodeRecord = null;
		int postalCode = 0;
		String selishte = null;
		String obshtina = null;
		String oblastenCentur = null;
		
		for(int i=0; i < addressTokens.length; i++){
			String addrToken = addressTokens[i].trim();
			//check if postal code: number, 4 digits
			if(addrToken.matches("[1-9][0-9]{3}")){
				postalCode = Integer.parseInt(addrToken);
				if(this.postCodes.containsKey(postalCode)){
					postCodeRecord = this.postCodes.get(postalCode); 
					selishte = postCodeRecord.get(2).replaceAll("^\"|\"$", "");
					obshtina = postCodeRecord.get(3).replaceAll("^\"|\"$", "");
					oblastenCentur = postCodeRecord.get(4).replaceAll("^\"|\"$", "");
				}
				break;
			}
		}
		
		if(postCodeRecord ==null){
			// Not all post codes are available - e.g. those for Sofia are missing.
			for(int i=0; i < addressTokens.length; i++){
				String addrToken = addressTokens[i].trim();
				if(!addrToken.matches("[1-9][0-9]{3}") && !addrToken.matches("(с\\.|жк|ж\\.к\\.|р\\-н|кв\\.|кв|общ\\.|общ|община|обл\\.|обл|област|мах\\.|к-с|кс|к/с|к\\.с\\.|местност|п\\.к\\.).*")){
					if(this.ekatteIndexByName.containsKey(addrToken)){
						if(selishte!=null){
							List<String> record = this.ekatteIndexByName.get(addrToken);
							oblastenCentur = record.get(3);
							obshtina = record.get(4);
							if(selishte.equals(oblastenCentur) || selishte.equals(obshtina)){
								selishte = addrToken;
							}							
						} else {
							selishte = addrToken;	
						}						
					}	
				}
			}
		}
		
		String streetNumber = null;
		String streetName = null;
		
		for(int i=0; i < addressTokens.length; i++){
			String addrToken = addressTokens[i].trim();
			Pattern streetNumberP = Pattern.compile("[1-9]{1,3}[\\u0430-\\u044f]{0,1}");//try to match street number - alphanumeric, >0, <999, can end with a letter
			if(!addrToken.matches("[1-9][0-9]{3}") && !addrToken.matches("(с\\.|жк|ж\\.к\\.|р\\-н|кв\\.|кв|общ\\.|общ|община|обл\\.|обл|област|мах\\.|к-с|кс|к/с|к\\.с\\.|местност|п\\.к\\.).*") &&
					!(addrToken.equals(selishte) || addrToken.equals(oblastenCentur) || addrToken.equals(obshtina))){
				// ул.14
				if(addrToken.startsWith("ул.") || addrToken.startsWith("Ул.") || addrToken.startsWith("ул")){
					String tmp = addrToken;
					tmp = tmp.replaceAll("(ул.|Ул.|ул)", "").trim();
					try{
						Integer.parseInt(tmp);//check if number
						streetName = tmp;
						continue;
					} catch(NumberFormatException  nfe){}
				}
				String streetTokens = addrToken.replace("бул.","")
											.replace("бул","")
											.replace("ул.","")
											.replace("Ул.","")
											.replace("УЛ.","")
											.replace("ул","")
											.replace("\"","");
				Matcher match = streetNumberP.matcher(streetTokens);
			    while (match.find()) {
			        streetNumber = match.group();
			    }
			    if(streetNumber==null && streetName==null){
			    	streetName = streetTokens;
			    } else if(streetName==null && streetTokens.indexOf(streetNumber)>-1){
					streetName = streetTokens.substring(0,streetTokens.indexOf(streetNumber));
					streetName = streetName.trim().replace("№","").replace("No","").replace("N","");
					if(streetName!=null && streetName.length()>0 && streetNumber!=null)
						System.out.print("");//TODO
					else {
						if(streetNumber==null)
							streetNumber = addressTokens[i].trim();
					}
				} 
			}
		}
		
		Address normalizedAddr = new Address();
		normalizedAddr.setCity(selishte);
		normalizedAddr.setPostalCode(postalCode);
		normalizedAddr.setStreetName(streetName);
		normalizedAddr.setStreetNumber(streetNumber);
		
		if(this.addressValidator!=null){
			Validation v = this.addressValidator.validate(normalizedAddr);
			if(v.hasErrors){
				if(this.strict){
					throw new AddressParseException(v.errors);
				} else {
					LOG.warn(v.errors.toString());
				}
			}
		}
		
		return normalizedAddr;
	}

}
