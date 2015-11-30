package opendata.tools.data;

import static org.junit.Assert.*;
import opendata.tools.data.Address;
import opendata.tools.data.AddressParseException;

import org.junit.Test;

public class AddressTest {

	@Test
	public void testParseStringString() {
		Address a = new Address("Банско", 2770, "България", "23");
		try {
			assertEquals(a, Address.parseString("Банско, Благоевград, Банско, 2770, ул.\"България\" 23"));
		} catch (AddressParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		a = new Address("Банско", 2770, "Цар Самуил", "29");
		try {
			assertEquals(a, Address.parseString("Банско, Благоевград, Банско, 2770, Цар Самуил 29"));
		} catch (AddressParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		a = new Address("Ямбол", 8660, "Жельо войвода", "2");
		try {
			assertEquals(a, Address.parseString("Ямбол, Ямбол, Ямбол, ул.Жельо войвода 2, 8600"));
		} catch (AddressParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		a = new Address("Ямбол", 8660, null, null);
		try {
			assertNotEquals(a, Address.parseString("Ямбол, Ямбол, Ямбол, к-с\"Златен рог\", 8600"));
		} catch (AddressParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

/*		System.out.println(Address.parseString("Шумен, Шумен, Шумен, местност СМЕСЕ, 9700"));
		System.out.println(Address.parseString("Шумен, Шумен, Дибич, 9811, с. Дибич"));
		System.out.println(Address.parseString("Хитрино, Шумен, Живково, 9794, с. Живково"));
		System.out.println(Address.parseString("Шумен, Никола Козлево, Църквица, 9939, ул.\"Хр.Ботев\""));
		System.out.println(Address.parseString("Хасково, Хасково, Хасково, 6303, ж. к. \"Орфей\""));
		System.out.println(Address.parseString("Хасково, Димитровград, Димитровград, 6400, кв. \"Изток\" ул. \"Изгрев\" 1"));
		System.out.println(Address.parseString("Търговище, Попово, Водица, 7851, ул. \"Д. Данов\""));
		System.out.println(Address.parseString("София, София-град, Столична, 1784, \"Младост 1\", сп.Окр.болница"));
		System.out.println(Address.parseString("София, София-град, Столична, жк. Люлин 10, бл. 120 П, 1335"));
		System.out.println(Address.parseString("Столична, София-град, София, 1797, ул.\"полк. Георги Янков\", 8-мо СОУ, ІV етаж"));
		System.out.println(Address.parseString("Столична, София-град, София, София кв.Красна поляна, ул. Суходолска 2 N 13, 1373"));
		System.out.println(Address.parseString("Столична, София-град, София, бул. Цар Борис III 224, сградата на ДИУУ, 1619"));
		System.out.println(Address.parseString("Гоце Делчев, Благоевград, Гоце Делчев, Гоце Делчев, бул. \"Гоце Делчев\", 13, 2900"));
*/	}

	@Test
	public void testParseStringStringAddressParser() {
		//fail("Not yet implemented");
	}

}
