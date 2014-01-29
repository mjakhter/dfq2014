package uk.ac.man.cs.img.dfq;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Lists;


/*
 * @author Suzanne M. Embury
 * 
 * This class groups together tests relating to the functionality which computes suspiciousness
 * scores for individual queries, and ranks a set of queries by descending suspiciousness
 */


public class SuspiciousQueryTest extends DFQTest {

	private String appWithQueriesName = "testSimpleContactDB";
	private String appWithQueriesPropertiesFilePath = DFQTest.getPropertiesFile(appWithQueriesName);
	
	private String appWithQueriesAndFailingTestsName = "testSimpleContactDBWithFaults";
	private String appWithQueriesAndFailingTestsPropertiesFilePath = DFQTest.getPropertiesFile(appWithQueriesAndFailingTestsName);

	
	@Test public void testComputesZeroSuspiciousnessForQueryNotExecutedByTestSuite() throws Exception {
		Application app = new Application(appWithQueriesAndFailingTestsName, appWithQueriesAndFailingTestsPropertiesFilePath);

		int expectedSuspiciousnessScore = 0;
		
		CodeMethod methodContainingQuery = app.getClassByName("ContactManager").
				                               getMethodByName("fetchSurnameMatchesUnexecuted");
		Query nonExecutedQuery = methodContainingQuery.getQueryByStartingLineNumber(149);
		
		assertEquals(expectedSuspiciousnessScore, nonExecutedQuery.getSuspiciousnessScore());		
	}
	
	
	@Test public void testComputesSuspiciousnesScoreForQueryOnlyExecutedByOnePassingTest() throws Exception {
		Application app = new Application(appWithQueriesAndFailingTestsName, appWithQueriesAndFailingTestsPropertiesFilePath);

		int expectedSuspiciousnessScore = XXX_JAVID_TO_COMPLETE_XXX;
		
		CodeMethod methodContainingQuery = app.getClassByName("ContactManager").
				                               getMethodByName("findMatches");
		Query queryExecutedInPassingTest = methodContainingQuery.getQueryByStartingLineNumber(60);
		
		assertEquals(expectedSuspiciousnessScore, queryExecutedInPassingTest.getSuspiciousnessScore());		
	}

	
	@Test public void testComputesSuspiciousnesScoreForQueryOnlyExecutedByOneFailingTest() throws Exception {
		Application app = new Application(appWithQueriesAndFailingTestsName, appWithQueriesAndFailingTestsPropertiesFilePath);

		int expectedSuspiciousnessScore = XXX_JAVID_TO_COMPLETE_XXX;
		
		CodeMethod methodContainingQuery = app.getClassByName("ContactManager").
				                               getMethodByName("getContactsByFullName");
		Query queryExecutedInPassingTest = methodContainingQuery.getQueryByStartingLineNumber(30);
		
		assertEquals(expectedSuspiciousnessScore, queryExecutedInPassingTest.getSuspiciousnessScore());		
	}
	
	
	@Test public void testComputesSuspiciousnesScoreForQueryExecutedByOneFailingTestAndTwoPassingTests() throws Exception {
		Application app = new Application(appWithQueriesAndFailingTestsName, appWithQueriesAndFailingTestsPropertiesFilePath);

		int expectedSuspiciousnessScore = XXX_JAVID_TO_COMPLETE_XXX;
		
		CodeMethod methodContainingQuery = app.getClassByName("ContactManager").
				                               getMethodByName("fetchSurnameMatches");
		Query queryExecutedInPassingTest = methodContainingQuery.getQueryByStartingLineNumber(145);
		
		assertEquals(expectedSuspiciousnessScore, queryExecutedInPassingTest.getSuspiciousnessScore());		
	}
	
	
	@Test public void testComputesSuspiciousnesScoreForQueryExecutedByOneFailingTestAndThreePassingTests() throws Exception {
		Application app = new Application(appWithQueriesAndFailingTestsName, appWithQueriesAndFailingTestsPropertiesFilePath);

		int expectedSuspiciousnessScore = XXX_JAVID_TO_COMPLETE_XXX;
		
		CodeMethod methodContainingQuery = app.getClassByName("ContactManager").
				                               getMethodByName("fetchMobileMatches");
		Query queryExecutedInPassingTest = methodContainingQuery.getQueryByStartingLineNumber(138);
		
		assertEquals(expectedSuspiciousnessScore, queryExecutedInPassingTest.getSuspiciousnessScore());		
	}
	
	
	// The above tests are the *must-have* tests for your thesis.  If you have time, it would be good to
	// get the following test could pass as well.
	
	@Test public void testFindsNoSuspiciousQueriesFromAppWithNoFailingTests() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		
		// Execute test
		List<Query> actualQueries = app.getSuspiciousQueries();
		
		// Check results
		assertEquals(0, actualQueries.size());
	}
	
	@Test public void testFindsXXXSuspiciousQueriesWhenSomeTestsFail() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesAndFailingTestsName, appWithQueriesAndFailingTestsPropertiesFilePath);
		
		// Define Expected Results JAVID TO COMPLETE
		Query expectedQuery1 = app.getClassByName(XXX_JAVID_TO_COMPLETE_CLASS_NAME_XXX).
				  				   getMethodByName(XXX_JAVID_TO_COMPLETE_METHOD_NAME_XXX).
				  				   getQueryByStartingLineNumber(XXX_JAVID_TO_COMPLETE_LINE_NUMBER_XXX);
		
		// Execute test
		List<Query> actualQueries = app.getSuspiciousQueries();
		
		// Check results JAVID TO COMPLETE
		assertThat(actualQueries, containsInAnyOrder(expectedQuery1, expectedQuery2, expectedQuery3));
	}
	
	@Test public void testFindsXXXSuspiciousQueriesWhenSomeTestsFailWithSuspiciousnessThreshold() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesAndFailingTestsName, appWithQueriesAndFailingTestsPropertiesFilePath);
		
		// Define Expected Results JAVID TO COMPLETE
		Query expectedQuery1 = app.getClassByName(XXX_JAVID_TO_COMPLETE_CLASS_NAME_XXX).
				  				   getMethodByName(XXX_JAVID_TO_COMPLETE_METHOD_NAME_XXX).
				  				   getQueryByStartingLineNumber(XXX_JAVID_TO_COMPLETE_LINE_NUMBER_XXX);
		
		// Execute test
		List<Query> actualQueries = app.getSuspiciousQueries(XXX_SUSPICIOUSNESS_THRESHOLD_JAVID_TO_COMPLETE_XXX);
		
		// Check results JAVID TO COMPLETE
		assertThat(actualQueries, containsInAnyOrder(expectedQuery1, expectedQuery2));
	}
	
	
	// Javid - feel free to change the contents of the diagnostic reports suggested in the tests
	// below.  You could change them to an XML format, or HTML, for easy display, if you preferred.
	
	@Test public void testCreatesNoDiagnosticReportWhenNoSuspiciousQueries() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);

		// Define Expected Results
		String expectedReport = "Dear " + appWithQueriesName + " Development Team,\n\n" +
								"There are no suspicious queries on this application at present.\n\n" +
								"Yours sincerely,\n\n" + 
								"The Automation Team";

		// Execute test
		String actualReport = app.getDiagnosticReport();
		
		// Check results
		assertThat(actualReport, equalToIgnoringWhiteSpace(expectedReport));
	}
	
	
	@Test public void testCreatesDiagnosticReportForSuspiciousQueries() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesAndFailingTestsName, appWithQueriesAndFailingTestsPropertiesFilePath);

		// Define Expected Results
		String expectedReport = "Dear " + appWithQueriesName + " Development Team,\n\n" +
								"There are " + XXX_NUMBER_OF_SUSPICIOUS_QUERIES_EXPECTED_XXX + "suspicious queries for " +
								"this application.  Details are given below:\n\n" +
								"\t Query at CLASS_NAME, METHOD_NAME, LINE_NUMBER has suspiciousness score SUSPICIOUSNESS_SCORE.\n" +
								"\t Query at CLASS_NAME, METHOD_NAME, LINE_NUMBER has suspiciousness score SUSPICIOUSNESS_SCORE.\n\n" +
								"Yours sincerely,\n\n" + 
								"The Automation Team";

		// Execute test
		String actualReport = app.getDiagnosticReport();
		
		// Check results
		assertThat(actualReport, equalToIgnoringWhiteSpace(expectedReport));
	}
	
	
	
	// Helper methods for the test cases
	
	// Convert a list of Statements into a List of their line numbers
	private Integer[] getLineNumbersForAllQueryStatements(Query query) {
		List<? extends Statement> stmts = query.getStatements();
		return Lists.newArrayList(Lists.transform(stmts, new Function<Statement, Integer>() {
			public Integer apply(Statement stmt) {
				return stmt.getLineNumber();
			}
		})).toArray(new Integer[0]);
	}
}
