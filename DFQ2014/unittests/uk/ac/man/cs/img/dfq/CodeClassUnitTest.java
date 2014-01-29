package uk.ac.man.cs.img.dfq;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/** @author Javid Akhter
 * Unit Tests for CodeClass class
 */

public class CodeClassUnitTest {

	private String appName = "testOnePassOneFail";
	private String propertiesFilePath = DFQTest.getPropertiesFile(appName);
	private String className = "Game";

	Application app;
	CodeClass codeClass;
	CodeMethod codeMethod;

	@Before
	public void setUp() throws Exception {
		this.app = new Application(appName, propertiesFilePath);
		this.codeClass = app.getClassByName(className);
	}

	@Test
	public void testCodeClass() throws Exception {
		assertEquals(appName, app.getName());
	}

	@Test
	public void testGetMethodByName() throws MethodWithGivenNameNotFoundException {
		String expMethodName = "getPlayerList";
		String actualMethodName = codeClass.getMethods().get(0).getName();

		assertEquals(expMethodName, actualMethodName);
	}

	@Test
	public void testGetName() {
		String expClassName = "Game";
		String actualClassName = codeClass.getName();

		assertEquals(4, actualClassName.length());
		assertEquals(expClassName, actualClassName);
	}

	@Test
	public void testGetMethods() {
		String expMethodName = "getPlayerList";
		String actulaMethodName = codeClass.getMethods().get(0).getName();

		assertEquals(13, actulaMethodName.length());
		assertEquals(expMethodName, actulaMethodName);
	}

	@Test
	public void testGetMethodBySignature() {
		String expMethodSignature = 
				"getPlayerList()[Luk/ac/man/cs/img/dfq/testsystems/tennis/Player;";
		String actualMethodSignature = codeClass.getMethods().get(0).getSignature();

		assertEquals(expMethodSignature, actualMethodSignature);
	}
}
