package uk.ac.man.cs.img.dfq;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/*
 * @author Suzanne M. Embury
 * 
 * This class groups together tests of the functionality which loads the model
 * of an execution of the application (i.e. a trace of an execution) into the
 * DFQ system.
 * 
 */

public class LoadTraceOfApplicationTest extends DFQTest {

	private String appName = "testOnePassOneFail";
	private String propertiesFilePath = DFQTest.getPropertiesFile(appName);

	@Test
	public void testFindTraceFileFromPropertiesFile() throws Exception {
		// Execute the test
		Application app = new Application(appName, propertiesFilePath);
	
		// Check the result
		assertEquals(DFQTest.getTraceFile(appName), app.getTrace().getTraceFilePath());
	}
	
	
	@Test(expected=TraceFileNotFoundException.class)
	public void testNonExistentTraceFileInPropertiesFile() throws Exception {
		new Application(appName, DFQTest.getPropertiesFileWithNonExistentTraceFile());
	}

	
	@Test
	public void testLoadProductionMethodSignaturesAndMapToMethodInstances() throws Exception {
		String expectedName = "setInactive";
		String expectedSignature = "setInactive()V";
		
		// Set up for the test
		Application app = new Application(appName, propertiesFilePath);
		
		// Execute the test
		CodeMethod method = app.getClassByName("Game").getMethodBySignature(expectedSignature);
		assertNotNull(method);
		
		// Check the results
		assertEquals(expectedName, method.getName());
		assertEquals(expectedSignature, method.getSignature());
	}
	
	
	@Test
	public void testLoadTestMethodSignaturesAndMapToMethodInstances() throws Exception {
		String expectedName = "testNewGame";
		String expectedSignature = "testNewGame()V";
		
		// Set up for the test
		Application app = new Application(appName, propertiesFilePath);

		// Execute the test
		CodeMethod method = app.getClassByName("GameTest").getMethodBySignature(expectedSignature);

		// Check the results
		assertEquals(expectedName, method.getName());
		assertEquals(expectedSignature, method.getSignature());
	}

	@Test
	public void testLoadExecutedTestsFromTrace() throws Exception {
		// Set up expected results
		String[] expectedExecTestNames = { "testGameResigned", "testNewGame" };
		
		// Set up for the test
		Application app = new Application(appName, propertiesFilePath);
		ApplicationTrace trace = app.getTrace();

		// Execute the test
		List<TestMethod> executedTests = trace.getExecutedTests();
		
		// Check the results
		String[] actualExecTestNames = getNamesForMethods(executedTests);
		Arrays.sort(actualExecTestNames);
		assertArrayEquals(expectedExecTestNames, actualExecTestNames);
	}
	
	@Test
	public void testFetchPassedTestsFromTrace() throws Exception {
		// Set up expected results
		String[] expectedPassedTestNames = { "testNewGame" };
		
		// Set up for the test
		Application app = new Application(appName, propertiesFilePath);
		ApplicationTrace trace = app.getTrace();

		// Execute the test
		List<TestMethod> passedTests = trace.getPassedTests();
		
		// Check the results
		String[] actualPassedTestNames = getNamesForMethods(passedTests);
		Arrays.sort(actualPassedTestNames);
		assertArrayEquals(expectedPassedTestNames, actualPassedTestNames);
	}

	
	@Test
	public void testFetchFailedTestsFromTrace() throws Exception {
		// Set up expected results
		String[] expectedFailedTestNames = { "testGameResigned" };
		
		// Set up for the test
		Application app = new Application(appName, propertiesFilePath);
		ApplicationTrace trace = app.getTrace();

		// Execute the test
		List<TestMethod> failedTests = trace.getFailedTests();
	
		// Check the results
		String[] actualFailedTestNames = getNamesForMethods(failedTests);
		Arrays.sort(actualFailedTestNames);
		assertArrayEquals(expectedFailedTestNames, actualFailedTestNames);
	}
	
	@Test
	public void testTestMethodByName() throws Exception {
		// Set up expected results
		String expectedMethod = "testGameResigned";

		// Set up for the test
		Application app = new Application(appName, propertiesFilePath);
		CodeMethod method = app.getTestMethodByName("testGameResigned");

		// Execute the test and check the results
		assertEquals(expectedMethod, method.getName());
	}
	
	@Test
	public void testFetchSpecificTestResultFromTrace() throws Exception {
		// Set up expected results
		Map<String, Boolean> expectedTestResults = Maps.newHashMap();
		expectedTestResults.put("testNewGame", true);
		expectedTestResults.put("testGameResigned", false);
	
		// Set up for the test
		Application app = new Application(appName, propertiesFilePath);
		ApplicationTrace trace = app.getTrace();
		List<TestMethod> executedTests = trace.getExecutedTests();	
		
		assertEquals(2, executedTests.size());
	
		TestMethod test1 = executedTests.get(0);
		TestMethod test2 = executedTests.get(1);
		
		// Execute the test and check the results
		//For each test, check that the resulting test is in the set of expected results, and 
		//then check that the result is what is expected.
		assertNotNull("can't find test1 in expected results", expectedTestResults.get(test1.getName()));
		assertEquals(expectedTestResults.get(test1.getName()), test1.testPassed());

		assertNotNull("can't find test 2 in expected results", expectedTestResults.get(test2.getName()));
		assertEquals(expectedTestResults.get(test2.getName()), test2.testPassed());	
	}
	
	
	@Test
	public void testFetchStatementsExecutedByNamedTestCase() throws Exception {
		Application app = new Application(appName, propertiesFilePath);
		CodeClass gameClass = app.getClassByName("Game");
		CodeClass playerClass = app.getClassByName("Player");
		TestClass gameTestClass = (TestClass) app.getClassByName("GameTest");
		
		Set<Statement> expectedStatements = new HashSet<Statement>();
		expectedStatements.add(gameTestClass.getStatementByLineNumber(21));
		expectedStatements.add(playerClass.getStatementByLineNumber(8));
		expectedStatements.add(playerClass.getStatementByLineNumber(16));
		expectedStatements.add(gameTestClass.getStatementByLineNumber(22));
		//expectedStatements.add(playerClass.getStatementByLineNumber(8));
		//expectedStatements.add(playerClass.getStatementByLineNumber(16));
		expectedStatements.add(gameTestClass.getStatementByLineNumber(23));

		expectedStatements.add(gameTestClass.getStatementByLineNumber(24));
		expectedStatements.add(gameClass.getStatementByLineNumber(11));
		expectedStatements.add(gameClass.getStatementByLineNumber(21));
		expectedStatements.add(gameClass.getStatementByLineNumber(12));
		expectedStatements.add(gameClass.getStatementByLineNumber(29));
		expectedStatements.add(gameClass.getStatementByLineNumber(13));
		expectedStatements.add(gameClass.getStatementByLineNumber(37));
		
		expectedStatements.add(gameTestClass.getStatementByLineNumber(25));
		expectedStatements.add(playerClass.getStatementByLineNumber(28));
		
		/* The remaining statements are not executed, because the test fails with a null pointer
		 * exception on the preceding line.
		expectedStatements.add(gameClass.getStatementByLineNumber(45));
		expectedStatements.add(gameClass.getStatementByLineNumber(41));
		
		expectedStatements.add(gameTestClass.getStatementByLineNumber(26));
		expectedStatements.add(gameClass.getStatementByLineNumber(33));
		*/
	
		// Set up for the test
		CodeMethod test = gameTestClass.getMethodByName("testGameResigned");
		
		// Execute the test
		List<Statement> executedStatements = test.getExecutedStatements();
		
		Set<Statement> actualExecutedStatements = new HashSet<Statement>(executedStatements);

		// Check the results		
		assertEquals(expectedStatements.size(), actualExecutedStatements.size());
		diagnoseProblems(actualExecutedStatements, expectedStatements);
		assertEquals(expectedStatements, actualExecutedStatements);
		
	}


	@Test
	public void testFetchTraceOfStatementsExecutedByNamedTestCase() throws Exception {
		Application app = new Application(appName, propertiesFilePath);
		CodeClass gameClass = app.getClassByName("Game");
		CodeClass playerClass = app.getClassByName("Player");
		TestClass gameTestClass = (TestClass) app.getClassByName("GameTest");
		
		List<Statement> expectedStatements = new ArrayList<Statement>();
		expectedStatements.add(gameTestClass.getStatementByLineNumber(21));
		expectedStatements.add(playerClass.getStatementByLineNumber(8));
		expectedStatements.add(playerClass.getStatementByLineNumber(16));
		expectedStatements.add(gameTestClass.getStatementByLineNumber(22));
		expectedStatements.add(playerClass.getStatementByLineNumber(8));
		expectedStatements.add(playerClass.getStatementByLineNumber(16));
		expectedStatements.add(gameTestClass.getStatementByLineNumber(23));

		expectedStatements.add(gameTestClass.getStatementByLineNumber(24));
		expectedStatements.add(gameClass.getStatementByLineNumber(11));
		expectedStatements.add(gameClass.getStatementByLineNumber(21));
		expectedStatements.add(gameClass.getStatementByLineNumber(12));
		expectedStatements.add(gameClass.getStatementByLineNumber(29));
		expectedStatements.add(gameClass.getStatementByLineNumber(13));
		expectedStatements.add(gameClass.getStatementByLineNumber(37));
		
		expectedStatements.add(gameTestClass.getStatementByLineNumber(25));
		expectedStatements.add(playerClass.getStatementByLineNumber(29));
	
		// Set up for the test
		CodeMethod test = gameTestClass.getMethodByName("testGameResigned");
		
		// Execute the test
		List<Statement> statementTrace = test.getTraceOfExecution();
		List<Statement> actualStatementTrace = new ArrayList<Statement>(statementTrace);
		
		// Check the results
		assertEquals(expectedStatements, actualStatementTrace);	
	}
	
	
	
	// Utility Functions
	
	/*
	 * Change the list of test methods provided into a list containing only the names of those methods
	 */
	private String[] getNamesForMethods(List<? extends CodeMethod> methods) {
		return Lists.newArrayList(Lists.transform(methods, new Function<CodeMethod, String>() {
			public String apply(CodeMethod method) {
				return method.getName();
			}
		})).toArray(new String[0]);
	}
	
	
	private void diagnoseProblems(Set<Statement> actualStmts, Set<Statement> expectedStmts) {
		if (expectedStmts.size() != actualStmts.size()) {
			Set<Statement> emptySet = new HashSet<Statement>();
			
			Set<Statement> falsePositives = new HashSet<Statement>(actualStmts);
			falsePositives.removeAll(expectedStmts);
			
			Set<Statement> falseNegatives = new HashSet<Statement>(expectedStmts);
			falseNegatives.removeAll(actualStmts);
			
			assertEquals("false positives found: ", emptySet, falsePositives);
			assertEquals("false negatives found: ", emptySet, falseNegatives);
		}
	}
	
}
