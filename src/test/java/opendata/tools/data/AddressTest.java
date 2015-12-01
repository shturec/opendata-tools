package opendata.tools.data;

import static org.junit.Assert.*;
import opendata.tools.data.Address;
import opendata.tools.data.AddressParseException;

import org.junit.Test;

public class AddressTest {

	@Test
	public void testParseValid() {
		Address a = new Address("Банско", 2770, "България", "23");
		try {
			assertEquals(a, Address.parseString("Банско, Благоевград, Банско, 2770, ул.\"България\" 23"));
			a = new Address("Банско", 2770, "Цар Самуил", "29");
			assertEquals(a, Address.parseString("Банско, Благоевград, Банско, 2770, Цар Самуил 29"));
			a = new Address("Дибич", 9811, null, null);
			assertEquals(a, Address.parseString("Шумен, Шумен, Дибич, 9811, с. Дибич"));
			a = new Address("Живково", 9794, null, null);
			assertEquals(a, Address.parseString("Хитрино, Шумен, Живково, 9794, с. Живково"));
			a = new Address("Църквица", 9939, "Хр.Ботев", null);
			assertEquals(a, Address.parseString("Шумен, Никола Козлево, Църквица, 9939, ул.\"Хр.Ботев\""));
		} catch (AddressParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testParseInvalid(){
		Address a = new Address("Ямбол", 8660, "Жельо войвода", "2");
		try {
			assertNotEquals(a, Address.parseString("Ямбол, Ямбол, Ямбол, ул.Жельо войвода 2, 8600"));
			a = new Address("Ямбол", 8660, null, null);
			assertNotEquals(a, Address.parseString("Ямбол, Ямбол, Ямбол, к-с\"Златен рог\", 8600"));
			a = new Address("Шумен", 9700, null, null);
			assertNotEquals(a, Address.parseString("Шумен, Шумен, Шумен, местност СМЕСЕ, 9700"));
			a = new Address("Хасково", 6303, null, null);
			assertNotEquals(a, Address.parseString("Хасково, Хасково, Хасково, 6303, ж. к. \"Орфей\""));
			a = new Address("Димитровград", 6400, "Изгрев", "1");
			assertNotEquals(a, Address.parseString("Хасково, Димитровград, Димитровград, 6400, кв. \"Изток\" ул. \"Изгрев\" 1"));
			a = new Address("Водица", 7851, "Д. Данов", null);
			assertNotEquals(a, Address.parseString("Търговище, Попово, Водица, 7851, ул. \"Д. Данов\""));
			a = new Address("София", 1784, null, null);
			assertNotEquals(a, Address.parseString("София, София-град, Столична, 1784, \"Младост 1\", сп.Окр.болница"));
			a = new Address("София", 1335, null, null);
			assertNotEquals(a, Address.parseString("София, София-град, Столична, жк. Люлин 10, бл. 120 П, 1335"));
			a = new Address("София", 1797, "\"полк. Георги Янков\"", null);
			assertNotEquals(a, Address.parseString("Столична, София-град, София, 1797, ул.\"полк. Георги Янков\", 8-мо СОУ, ІV етаж"));
			a = new Address("София", 1373, "Суходолска", "13");
			assertNotEquals(a, Address.parseString("Столична, София-град, София, София кв.Красна поляна, ул. Суходолска 2 N 13, 1373"));
			a = new Address("София", 1619, "Цар Борис III", "224");
			assertNotEquals(a, Address.parseString("Столична, София-град, София, бул. Цар Борис III 224, сградата на ДИУУ, 1619"));
			a = new Address("Гоце Делчев", 2900, "Гоце Делчев", "13");
			assertNotEquals(a, Address.parseString("Гоце Делчев, Благоевград, Гоце Делчев, Гоце Делчев, бул. \"Гоце Делчев\", 13, 2900"));
		} catch (AddressParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEqualsReturnTrue(){
		assertEquals(new Address(), new Address());
		assertEquals(new Address(null,0,null,null), new Address(null,0,null,null));
		assertEquals(new Address("София",1000,"България","120A"), new Address("София",1000,"България","120A"));
		assertEquals(new Address(null,1000,"България","120A"), new Address(null,1000,"България","120A"));
		assertEquals(new Address("София",1000,null,"120A"), new Address("София",1000,null,"120A"));
		assertEquals(new Address("София",1000,"България",null), new Address("София",1000,"България",null));
	}
	
	
	@Test
	public void testEqualsReturnFalse(){
		assertNotEquals(new Address("София",0,null,null), new Address(null,0,null,null));
		assertNotEquals(new Address(null,1000,null,null), new Address(null,0,null,null));
		assertNotEquals(new Address(null,1000,"България",null), new Address(null,0,null,null));
		assertNotEquals(new Address(null,1000,null,"120"), new Address("Варна",1220,"Опълченска","38"));
		assertNotEquals(new Address("София",1000,"България","120A"), new Address("София",1000,"България","120"));
		assertNotEquals(new Address("София",1000,"България","120A"), new Address("Варна",1000,"България","120A"));
		assertNotEquals(new Address("София",1000,"България","120A"), new Address("София",1220,"България","120A"));
		assertNotEquals(new Address("София",1000,"България","120A"), new Address("София",1000,"Опълченска","120A"));
		assertNotEquals(new Address("София",1000,"България","120A"), new Address("София",1000,"България","38"));
	}

}
