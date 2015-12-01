package opendata.tools.spatial.mapquest;

import opendata.tools.data.Address;

import org.junit.Test;

import com.google.gson.JsonObject;

public class RequestLocationTest {

	@Test
	public void testToJSONString() {
		RequestLocation rLoc = new RequestLocation();
		System.out.println(rLoc.toJSONString(new Address("Sofia", 1220, "Цариградско шосе", "183")));
	}
	
	@Test
	public void testFromJSON() {
		RequestLocation rLoc = new RequestLocation();
		JsonObject obj = new JsonObject();
		obj.addProperty("city", "София");
		obj.addProperty("postalCode", 1220);
		obj.addProperty("street", "183 Цариградско шосе");
		System.out.println(rLoc.fromJSON(obj));
	}

}
