package uk.ac.man.cs.img.dfq;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/* @Author Javid Akhter
 * 
 * Unit test cases for Application class of the DFQ System.
 */

public class ApplicationUnitTest {

	private String appName = "testOnePassOneFail";
	private String propertiesFilePath = DFQTest.getPropertiesFile(appName);

	Application app;

	@Before
	public void setUp() throws Exception {
		this.app = new Application(appName, propertiesFilePath);
	}

	@Test
	public void testApplicationForPropertiesFilePath() {
		assertEquals(propertiesFilePath, app.getPropertiesFilePath());
	}

	@Test
	public void testApplicationForTraceFilePath() {
		String expectedTraceFile = 
				"E:\\phdworkspace\\testOnePassOneFail\\testOnePassOneFail.xml";

		assertEquals(expectedTraceFile, app.getTrace().getTraceFilePath());
	}

	@Test
	public void testGetName() throws Exception {
		String expectedApplicationName = "testOnePassOneFail";

		assertEquals(expectedApplicationName, app.getName());
	}

	@Test
	public void testGetProductionClassByName() throws Exception {
		String expClassName = "Game";

		assertEquals(expClassName, app.getClassByName(expClassName).getName());
	}

	@Test
	public void testGetTestClassByName() throws Exception {
		String expTestClassName = "GameTest";

		assertEquals(expTestClassName, app.getClassByName(expTestClassName).getName());
	}

	@Test
	public void testGetExecutedTests() throws Exception {
		assertEquals(2, app.getTrace().getExecutedTests().size()); 
	}

	@Test
	public void testGetClasses() {
		String[] expectedClassNames = { "Game", "GameTest", "Player" };

		assertEquals(expectedClassNames.length, app.getClasses().size());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetProductionClasses() throws Exception {
		String[] expectedClassNames = {"Game", "Player"};

		List<String> actualClassNames = getNamesForAllClasses(app.getProductionClasses());

		assertEquals(2, app.getProductionClasses().size());
		assertEquals(expectedClassNames, actualClassNames.toArray());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetTestClasses() {
		String[] expectedClassNames = {"GameTest"};

		List<String> actualClassNames = getNamesForAllClasses(app.getTestClasses());

		assertEquals(1, app.getTestClasses().size());
		assertEquals(expectedClassNames, actualClassNames.toArray());
	}

	// Utility Functions

	/* 
	 * Change the list of classes provided into a list containing only the names of those classes
	 */
	private List<String> getNamesForAllClasses(List<? extends CodeClass> classes) {
		return Lists.newArrayList(Lists.transform(classes, new Function<CodeClass, String>() {
			public String apply(CodeClass codeClass) {
				return codeClass.getName();
			}
		}));
	}
}
