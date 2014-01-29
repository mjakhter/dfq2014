package uk.ac.man.cs.img.dfq;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/* @author Suzanne M. Embury
 * 
 * The tests on this class are concerned with the correctness of the model of the
 * application to be diagnosed that is created when a new Application object
 * is created.  Other aspects of the functionality of Application are exercised
 * in other test classes (in particular, the correctness of the model of a trace
 * associated with an application, and the correctness of the resulting diagnosis).
 */

public class LoadApplicationTest extends DFQTest {

	private String appName = "testOnePassOneFail";
	private String propertiesFilePath = DFQTest.getPropertiesFile(appName);

	
	/* Basic Application object creation tests */
	
	@Test
	public void testGetDetailsOfNewApplication() throws Exception {
		// Execute the test
		Application app = new Application(appName, propertiesFilePath);

		// Check the result
		assertEquals(appName, app.getName());
		assertEquals(propertiesFilePath, app.getPropertiesFilePath());
	}
	
	
	@Test(expected = CouldNotFindPropertiesFileException.class)
	public void testReportsAttemptToReadNonExistentPropertiesFile() throws Exception {
		new Application(appName, propertiesFilePath + "does.not.exist");
	}
	
	
	@Test(expected = CouldNotReadPropertiesFileException.class)
	public void testReportsAttemptToReadNonPropertiesFileAsPropertiesFile() throws Exception {
		new Application(appName, DFQTest.nonPropertiesFilePath());
	}
	
	
	
	/* Tests that classes are loaded correctly */
	
	@Test
	public void testAllClassesAreLoadedFromSimpleApplication() throws Exception {
		// Set up the expected result
		String[] expectedClassNames = { "Game", "GameTest", "Player" };
		
		// Set up the system ready for test
		Application app = new Application(appName, propertiesFilePath);

		// Execute the test
		List<String> actualClassNames = getNamesForAllClasses(app.getClasses());
		
		// Check results
		sortAlphabetically(actualClassNames);
		assertArrayEquals(expectedClassNames, actualClassNames.toArray(new String[0]));
	}

	
	@Test
	public void testAllProductionClassesAreLoadedFromSimpleApplication() throws Exception {
		// Set up the expected result
		String[] expectedClassNames = { "Game", "Player" };
		
		// Set up system ready for test
		Application app = new Application(appName, propertiesFilePath);

		// Execute the test
		List<String> actualClassNames = getNamesForAllClasses(app.getProductionClasses());
		
		// Check results
		sortAlphabetically(actualClassNames);
		assertArrayEquals(expectedClassNames, actualClassNames.toArray(new String[0]));
	}


	@Test
	public void testAllTestClassesAreLoadedFromSimpleApplication() throws Exception {
		// Set up the expected result
		String expectedClassName = "GameTest";
		
		// Set up system ready for test
		Application app = new Application(appName, propertiesFilePath);

		// Execute the test
		List<TestClass> actualTestClasses = app.getTestClasses();
		
		// Check the results
		assertEquals(1, actualTestClasses.size());
		assertEquals(expectedClassName, ((TestClass) actualTestClasses.get(0)).getName());
	}
	
	
	
	/* Tests that methods are loaded correctly */
	
	@Test
	public void testAllMethodsAreLoadedFromSimpleApplication() throws Exception {
		// Set up the expected result
		Map<String, String[]> expectedMethodNames = Maps.newHashMap();
		expectedMethodNames.put("Game", new String[] { "Game",
													   "getGameType",
													   "getPlayerList",
													   "isActive",
													   "resignGame",
													   "setActive",
													   "setGameType",
													   "setInactive",
													   "setPlayerList" }
													   
		);
		expectedMethodNames.put("Player", new String[] { "getCurrentGame",
														 "getName",
														 "Player",
														 "resignGame",
														 "setCurrentGame",
														 "setName" } 
														 
		);
		expectedMethodNames.put("GameTest", new String[] { "testGameResigned",
														   "testNewGame"}
														   
		);
		
		
		// Set up system ready for test
		Application app = new Application(appName, propertiesFilePath);
		List<CodeClass> classes = app.getClasses();
		
		assertEquals(3, classes.size());
		CodeClass class0 = classes.get(0);
		CodeClass class1 = classes.get(1);
		CodeClass class2 = classes.get(2);

		// Execute the test
		List<String> actualMethodNamesForClass0 = getNamesForMethods(class0.getMethods());
		List<String> actualMethodNamesForClass1 = getNamesForMethods(class1.getMethods());
		List<String> actualMethodNamesForClass2 = getNamesForMethods(class2.getMethods());
		
		// Check results
		String[] expectedMethodNamesForClass0 = expectedMethodNames.get(class0.getName());
		String[] expectedMethodNamesForClass1 = expectedMethodNames.get(class1.getName());
		String[] expectedMethodNamesForClass2 = expectedMethodNames.get(class2.getName());
		
		sortAlphabetically(actualMethodNamesForClass0);
		sortAlphabetically(actualMethodNamesForClass1);
		sortAlphabetically(actualMethodNamesForClass2);
		
		assertArrayEquals(expectedMethodNamesForClass0, actualMethodNamesForClass0.toArray(new String[0]));
		assertArrayEquals(expectedMethodNamesForClass1, actualMethodNamesForClass1.toArray(new String[0]));
		assertArrayEquals(expectedMethodNamesForClass2, actualMethodNamesForClass2.toArray(new String[0]));
	}
	
	
	// TODO We need a test that includes a class with no methods, plus a class containing only a constructor
	
	
	
	// Test that start and end lines are returned correctly

	@Test
	public void testFetchConstructorMethodsStartAndEndLines() throws Exception {
		// Set up the expected result
		String className = "Game";
		int expectedStart = 11;
		int expectedEnd = 13;
		
		// Set up the system ready for test
		Application app = new Application(appName, propertiesFilePath);
		CodeMethod method = app.getClassByName(className).getMethodByName(className);
		
		// Execute the test and check results
		assertEquals("start line", expectedStart, method.getStartLineNumber());
		assertEquals("end line", expectedEnd, method.getEndLineNumber());
	}
	
	@Test
	public void testFetchAnotherConstructorMethodsStartAndEndLines() throws Exception {
		// Set up the expected result
		String className = "Player";
		int expectedStart = 8;
		int expectedEnd = 8;
		
		// Set up the system ready for test
		Application app = new Application(appName, propertiesFilePath);
		CodeMethod method = app.getClassByName(className).getMethodByName(className);
		
		// Execute the test and check results
		assertEquals("start line", expectedStart, method.getStartLineNumber());
		assertEquals("end line", expectedEnd, method.getEndLineNumber());
	}

	@Test
	public void testFetchSingleLineMethodStartAndEndLines() throws Exception {
		// Set up the expected result
		String className = "Player";
		String methodName = "getCurrentGame()";
		int expectedStart = 20;
		int expectedEnd = 20;
		
		// Set up the system ready for test
		Application app = new Application(appName, propertiesFilePath);
		CodeMethod method = app.getClassByName(className).getMethodByName(methodName);
		
		// Execute the test and check results
		assertEquals(expectedStart, method.getStartLineNumber());
		assertEquals(expectedEnd, method.getEndLineNumber());
	}
	
	
	/* Tests that statements are loaded correctly */
	
	@Test
	public void testGetStatementByLineNumber() throws Exception {				
		// Set up expected results
		int lineNumber = 12;
		String expectedStatement = "setPlayerList(playerList);";
		int expStartLine = 45;
		String expStartStatement = "setInactive();";

		// Set up system ready for test
		Application app = new Application(appName, propertiesFilePath);
		CodeClass codeClass = app.getClassByName("Game");
		CodeMethod method = codeClass.getMethodByName("resignGame");

		// Execute the test
		String actualStatement = codeClass.getStatementByLineNumber(lineNumber).getStatementText();

		// Check the results
		assertEquals(expectedStatement, actualStatement);
		assertEquals(expStartLine, method.getStartLineNumber());
		assertEquals(expStartStatement, method.getStatementByLineNumber(expStartLine).getStatementText());
	}
	
	@Test
	public void testFetchSingleStatementFromProductionClassMethod() throws Exception {
		// This test case has been written assuming the final desired model
		// of the programme statements.  Implementing these methods against
		// the temporary array approach to modelling statements may be a
		// challenge, and may necessitate changes to this test case.
		
		// Set up expected results
		int lineNumber = 17;
		String expectedStatement = "return this.gameType;";

		// Set up system ready for test
		Application app = new Application(appName, propertiesFilePath);
		CodeMethod method = app.getClassByName("Game").getMethodByName("getGameType");
		
		// Execute the test
		String actualStatement = method.getStatementByLineNumber(lineNumber).getStatementText();
	
		// Check the results
		assertEquals(expectedStatement, actualStatement);
	}
	
	@Test
	public void testFetchSingleStatementFromTestClassMethod() throws Exception {
		// This test case has been written assuming the final desired model
		// of the programme statements.  Implementing these methods against
		// the temporary array approach to modelling statements may be a
		// challenge, and may necessitate changes to this test case.
		
		// Set up expected results
		int lineNumber = 22;
		String expectedStatement = "Player player2 = new Player(\"Joan\");";

		// Set up system ready for test
		Application app = new Application(appName, propertiesFilePath);
		CodeMethod method = app.getClassByName("GameTest").getMethodByName("testGameResigned");
		
		// Execute the test
		Statement statement = method.getStatementByLineNumber(lineNumber);
	
		// Check the results
		assertSame(method, statement.getCodeMethod());
		assertEquals(lineNumber, statement.getLineNumber());
		assertEquals(expectedStatement, statement.getStatementText());
	}
	
	
	@Test
	public void testAttemptToFetchSingleStatementOutwithTheGivenMethod() throws Exception {
		int lineNumber = 9;

		// Set up system ready for test
		try {
			Application app = new Application(appName, propertiesFilePath);
			CodeMethod method = app.getClassByName("GameTest").getMethodByName("testNewGame");

			// Execute the test
			method.getStatementByLineNumber(lineNumber);
			fail("AttemptToAccessStatementOutwithGivenMethodException expected but not thrown.");
		} catch (AttemptToAccessStatementOutwithGivenMethodException ex) {
			// Test succeeds
		}
	}
	
	@Test
	public void testFetchFirstStatementFromProductionClassMethod() throws Exception {
		// This test case has been written assuming the final desired model
		// of the programme statements.  Implementing these methods against
		// the temporary array approach to modelling statements may be a
		// challenge, and may necessitate changes to this test case.
		
		// Set up expected results
		int lineNumber = 29;
		String expectedStatement = "this.playerList = playerList;";

		// Set up system ready for test
		Application app = new Application(appName, propertiesFilePath);
		CodeMethod method = app.getClassByName("Game").getMethodByName("setPlayerList");
		
		// Execute the test
		Statement statement = method.getStatementByLineNumber(lineNumber);

		// Check the results
		assertSame(method, statement.getCodeMethod());
		assertEquals(lineNumber, statement.getLineNumber());
		assertEquals(expectedStatement, statement.getStatementText());
	}
	
	
	// TODO write a test that runs on a non-test method but does the same as this one.
	// annotations in test code seem to be causing a problem in Tarantula.
	
	
	@Test
	public void testFetchMultipleStatementsFromProductionClassMethod() throws Exception {
		// This test case has been written assuming the final desired model
		// of the programme statements.  Implementing these methods against
		// the temporary array approach to modelling statements may be a
		// challenge, and may necessitate changes to this test case.
		
		// Set up expected results
		Map<Integer, String> expectedStatements = Maps.newHashMap();
		expectedStatements.put(25, "return playerList;");

		// Set up system ready for test
		Application app = new Application(appName, propertiesFilePath);
		CodeMethod method = app.getClassByName("Game").getMethodByName("getPlayerList");
		
		// Execute the test
		Map<Integer, Statement> actualStatements = method.getStatements();
		
		// Check the results.  The maps should contain the same number of things, ... and the same things.
		// We don't want there to be extra stuff in actualStatements that we are not expecting to see.
		assertEquals(expectedStatements.size(), actualStatements.size());
		Statement actualStatement;
		for (Map.Entry<Integer, String> expectedStatement : expectedStatements.entrySet()) {
			actualStatement = actualStatements.get(expectedStatement.getKey());
			assertNotNull(actualStatement);
			assertSame(method, actualStatement.getCodeMethod());
			assertEquals(expectedStatement.getValue(), actualStatement.getStatementText());
		}
	}

	@Test
	public void testFetchMultipleStatementsFromTestClassMethod() throws Exception {
		// This test case has been written assuming the final desired model
		// of the programme statements.  Implementing these methods against
		// the temporary array approach to modelling statements may be a
		// challenge, and may necessitate changes to this test case.
		
		// Set up expected results
		Map<Integer, String> expectedStatements = Maps.newHashMap();
		expectedStatements.put(12, "Player player1 = new Player(\"Fred\");");
		expectedStatements.put(13, "Player player2 = new Player(\"Joan\");");
		expectedStatements.put(14, "Player[] playerList = { player1, player2 };");
		expectedStatements.put(15, "Game game = new Game(Game.SINGLES, playerList);");
		expectedStatements.put(16, "assertTrue(game.isActive());");
		expectedStatements.put(17, "assertEquals(Game.SINGLES, game.getGameType());");

		// Set up system ready for test
		Application app = new Application(appName, propertiesFilePath);
		CodeMethod method = app.getClassByName("GameTest").getMethodByName("testNewGame");
		
		// Execute the test
		Map<Integer, Statement> actualStatements = method.getStatements();
		// Check the results.  The maps should contain the same number of things, ... and the same things.
		// We don't want there to be extra stuff in actualStatements that we are not expecting to see.
		assertEquals(expectedStatements.size(), actualStatements.size());
		Statement actualStatement;
		for (Map.Entry<Integer, String> expectedStatement : expectedStatements.entrySet()) {
			actualStatement = actualStatements.get(expectedStatement.getKey());
			assertNotNull(actualStatement);
			assertSame(method, actualStatement.getCodeMethod());
			assertEquals(expectedStatement.getValue(), actualStatement.getStatementText());
		}
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
	
	/*
	 * Change the list of methods provided into a list containing only the names of those methods
	 */
	private List<String> getNamesForMethods(List<CodeMethod> methods) {
		return Lists.newArrayList(Lists.transform(methods, new Function<CodeMethod, String>() {
			public String apply(CodeMethod method) {
				return method.getName();
			}
		}));
	}
	
}
