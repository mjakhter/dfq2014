package uk.ac.man.cs.img.dfq;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;


/*
 * @author Suzanne M. Embury
 * 
 * This class groups together tests relating to the functionality which identifies
 * an initial set of candidate queries for the failed test cases.
 */


public class CandidateQueryTest extends DFQTest {

	private String noQueryAppName = "testOnePassOneFail";
	private String noQueryAppPropertiesFilePath = DFQTest.getPropertiesFile(noQueryAppName);

	private String appWithQueriesName = "testSimpleContactDB";
	private String appWithQueriesPropertiesFilePath = DFQTest.getPropertiesFile(appWithQueriesName);
	
	private String appWithQueriesAndFailingTestsName = "testSimpleContactDBWithFaults";
	private String appWithQueriesAndFailingTestsPropertiesFilePath = DFQTest.getPropertiesFile(appWithQueriesAndFailingTestsName);

	
	@Test public void testFindsNoQueriesInExecutedTestThatDoesNotExecuteQueries() throws Exception {
		// Set up system ready for test
		Application app = new Application(noQueryAppName, noQueryAppPropertiesFilePath);
		CodeMethod test = app.getTestMethodByName("testGameResigned");
		
		// Execute test
		List<Query> queries = test.getExecutedQueries();
		
		// Check results
		assertEquals(0, queries.size());
	}
	
	
	@Test public void testFindsSingleStatementQueryInExecutedTestCaseWithNoBranches() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		CodeMethod test = app.getTestMethodByName("testContactsWithDuplicateEmailAddressessNotPermitted");
		
		// Define Expected Results
		Query expectedQuery = app.getClassByName("ContactManager").
								  getMethodByName("getContactsByEmail").
								  getQueryByStartingLineNumber(40);
		
		// Execute test
		List<Query> actualQueries = test.getExecutedQueries();
		
		// Check results
		assertThat(actualQueries, contains(expectedQuery));
	}


	@Test public void testFindsMultiStatementQueryInExecutedTestCaseWithNoBranches() throws Exception {
		// I'm not sure this test is really needed, now that we have split out the tests about
		// the identification of query statements into LoadApplicationQueryTest.  We may want to delete
		// this test, but I think I'd want to see both test classes passing all tests before deciding
		// it is redundant. -SME
		
		
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		CodeMethod test = app.getTestMethodByName("testNewContactRetrievableAfterSuccessfulUpdate");
		
		// Define Expected Results
		Query expectedQuery = app.getClassByName("ContactManager").
								  getMethodByName("getContactsByFullName").
								  getQueryByStartingLineNumber(26);
		
		// Execute test
		List<Query> actualQueries = test.getExecutedQueries();
		
		// Check results
		assertThat(actualQueries, contains(expectedQuery));
	}
	
	
	@Test public void testFindsOnlyTheQueryInTheExecutedBranchOfTest() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		CodeMethod test = app.getTestMethodByName("testFindCloseMatchesWithContactBasedOnMobileAlone");
		
		// Define Expected Results
		Query expectedQuery = app.getClassByName("ContactManager").
				  				  getMethodByName("findMatchesWithSelectedSearchCriteria").
				  				  getQueryByStartingLineNumber(84);
		
		// Execute test
		List<Query> actualQueries = test.getExecutedQueries();
		
		// Check results
		assertThat(actualQueries, contains(expectedQuery));
	}
	
	
	@Test public void testFindsMultipleQueriesForExecutedTestCase() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		CodeMethod test = app.getTestMethodByName("testFindCloseMatchesWithContact");

		// Set up expected values
		Query expectedQuery1 = app.getClassByName("ContactManager").
								   getMethodByName("findMatches").
								   getQueryByStartingLineNumber(56);
		Query expectedQuery2 = app.getClassByName("ContactManager").
								   getMethodByName("findMatches").
								   getQueryByStartingLineNumber(60);
		Query expectedQuery3 = app.getClassByName("ContactManager").
				  				   getMethodByName("findMatches").
				  				   getQueryByStartingLineNumber(63);
		
		// Execute test
		List<Query> actualQueries = test.getExecutedQueries();
		
		// Check results
		assertThat(actualQueries, containsInAnyOrder(expectedQuery1, expectedQuery2, expectedQuery3));
	}
	
	
	@Test public void testFindsAllQueriesExecutedAcrossMultipleMethodCalls() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		CodeMethod test = app.getTestMethodByName("testFindCloseMatchesWithContactBasedOnMobileAndSurname");

		// Set up expected values
		Query expectedQuery1 = app.getClassByName("ContactManager").
								   getMethodByName("fetchSurnameMatches").
								   getQueryByStartingLineNumber(135);
		Query expectedQuery2 = app.getClassByName("ContactManager").
								   getMethodByName("fetchMobileMatches").
								   getQueryByStartingLineNumber(142);
		// Execute test
		List<Query> actualQueries = test.getExecutedQueries();
		
		// Check results
		assertThat(actualQueries, containsInAnyOrder(expectedQuery1, expectedQuery2));
	}


	@Test public void testIdentifyNoCandidateQueriesForPassingTestSuite() throws Exception {
		// Set up system ready for test (this application passes all its tests)
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		ApplicationTrace trace = app.getTrace();
		
		// Execute test
		Map<TestMethod, List<Query>> candidateQueries = trace.identifyAllCandidateQueries();
		
		// Check results
		assertEquals(0, candidateQueries.size());	
	}
	
	
	@Test public void testIdentifyMultipleCandidateQueriesForFailingTestSuite() throws Exception {
		// This application fails two of its tests: findMatchesWithSelectedSearchCriteria and 
		// testContactsWithDuplicateEmailAddressessNotPermitted
		
		// Set up system ready for test (this application fails two of its tests)
		Application app = new Application(appWithQueriesAndFailingTestsName, appWithQueriesAndFailingTestsPropertiesFilePath);
		ApplicationTrace trace = app.getTrace();
		
		// Set up expected result.
		TestMethod failingTest1 = app.getTestMethodByName("testNewContactRetrievableAfterSuccessfulUpdate");
		CodeMethod testedMethod1 = app.getClassByName("ContactManager").getMethodByName("getContactsByFullName");
		Query candidateQuery1 = testedMethod1.getQueryByStartingLineNumber(30);
		
		TestMethod failingTest2 = app.getTestMethodByName("testContactsWithDuplicateEmailAddressessNotPermitted");
		CodeMethod testedMethod2 = app.getClassByName("ContactManager").getMethodByName("getContactsByEmail");
		Query candidateQuery2 = testedMethod2.getQueryByStartingLineNumber(41);

		// Execute test
		Map<TestMethod, List<Query>> candidateQueries = trace.identifyAllCandidateQueries();

		// Check results
		assertThat(candidateQueries.keySet(), containsInAnyOrder(failingTest1, failingTest2));
		assertThat(candidateQueries.get(failingTest1), contains(candidateQuery1));
		assertThat(candidateQueries.get(failingTest2), contains(candidateQuery2));
	}

}
