package uk.ac.man.cs.img.dfq;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;


/*
 * @author Suzanne M. Embury
 * 
 * This class groups together tests relating to the functionality which identifies
 * the statements that make up a query in the loaded application, and supports querying
 * over them.
 */


public class LoadApplicationQueriesTest extends DFQTest {

	private String noQueryAppName = "testOnePassOneFail";
	private String noQueryAppPropertiesFilePath = DFQTest.getPropertiesFile(noQueryAppName);

	private String appWithQueriesName = "testSimpleContactDB";
	private String appWithQueriesPropertiesFilePath = DFQTest.getPropertiesFile(appWithQueriesName);
	
	
	@Test public void testFindsNoQueriesInApplicationThatDoesNotContainAnyQueries() throws Exception {
		// Set up system ready for test
		Application app = new Application(noQueryAppName, noQueryAppPropertiesFilePath);
		
		List<CodeMethod> methods = app.getAllMethods();
		
		for (CodeMethod method : methods) {
			// Execute test
			List<Query> queries = method.getQueries();
		
			// Check results
			assertEquals(0, queries.size());
		}
	}
	
	
	@Test public void testFindsOneSingleStatementQueryInMethodThatContainsOnlyOneQuery() throws Exception {
		// Define Expected Results
		String expectedClassName = "ContactManager";
		String expectedMethodName = "getContactsByEmail";
		Integer[] expectedQueryLines = { 39 };
		
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		CodeMethod method = app.getClassByName(expectedClassName).getMethodByName(expectedMethodName);
		
		// Execute test
		List<Query> actualQueries = method.getQueries();
		
		// Check results
		assertEquals(1, actualQueries.size());
		Query actualQuery = actualQueries.get(0);
		assertEquals(expectedClassName, actualQuery.getClassName());
		assertEquals(expectedMethodName, actualQuery.getMethodName());
		assertArrayEquals(expectedQueryLines, getLineNumbersForAllQueryStatements(actualQuery));
	}

	
	@Test public void testFindsOneMultiStatementQueryInMethodThatContainsOneQuery() throws Exception {		
		// Define Expected Results
		String expectedClassName = "ContactManager";
		String expectedMethodName = "getContactsByFullName";
		//Integer[] expectedQueryLines = { 26, 27, 31 };
		Integer[] expectedQueryLines = { 25, 26, 30 };
		
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		CodeMethod method = app.getClassByName(expectedClassName).getMethodByName(expectedMethodName);
		
		// Execute test
		List<Query> actualQueries = method.getQueries();
		
		// Check results
		assertEquals(1, actualQueries.size());
		Query actualQuery = actualQueries.get(0);
		assertEquals(expectedClassName, actualQuery.getClassName());
		assertEquals(expectedMethodName, actualQuery.getMethodName());
		assertArrayEquals(expectedQueryLines, getLineNumbersForAllQueryStatements(actualQuery));
	}
	
	
	@Test public void testFindsThreeQueriesInMethodThatContainsThreeQueries() throws Exception {
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);

		// Set up expected values
		CodeMethod method = app.getClassByName("ContactManager").getMethodByName("findMatches");
		Query expectedQuery1 = method.getQueryByStartingLineNumber(55);
		Query expectedQuery2 = method.getQueryByStartingLineNumber(59);
		Query expectedQuery3 = method.getQueryByStartingLineNumber(62);
		
		// Execute test
		List<Query> actualQueries = method.getQueries();
		
		// Check results
		assertEquals(3, actualQueries.size());
		assertThat(actualQueries, containsInAnyOrder(expectedQuery1, expectedQuery2, expectedQuery3));
	}
	
	
	@Test public void testFindsQueryGivenItsClassAndMethodAndStartingLine() throws Exception {
		// Define Expected Results
		String expectedClassName = "ContactManager";
		String expectedMethodName = "getContactsByFullName";
		//Integer[] expectedQueryLines = { 26, 27, 31 };
		Integer[] expectedQueryLines = { 25, 26, 30 };
		
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		CodeMethod method = app.getClassByName(expectedClassName).getMethodByName(expectedMethodName);
		
		// Execute test
		Query query = method.getQueryByStartingLineNumber(27);
		
		// Check results
		assertEquals(expectedClassName, query.getClassName());
		assertEquals(expectedMethodName, query.getMethodName());
		assertArrayEquals(expectedQueryLines, getLineNumbersForAllQueryStatements(query));
	}

	@Test public void testCantFindQueryGivenItsClassAndMethodAndInternalLineNumber() throws Exception {
		// Define Expected Results
		String expectedClassName = "ContactManager";
		String expectedMethodName = "getContactsByFullName";
		Integer[] expectedQueryLines = { 26, 27, 31 };
		
				
		// Set up system ready for test
		try {
			Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
			CodeMethod method = app.getClassByName(expectedClassName).getMethodByName(expectedMethodName);

			method.getQueryByStartingLineNumber(26);
			fail("QueryWithGivenStartNumberNotFoundException expected");
		} catch (QueryWithGivenStartNumberNotFoundException ex) {
			//Test succeeds 
		}
	}
	
	@Test public void testCantFindQueryGivenItsClassAndMethodAndInvalidLineNumber() throws Exception {
		// Define Expected Results
		String expectedClassName = "ContactManager";
		String expectedMethodName = "getContactsByFullName";
		Integer[] expectedQueryLines = { 26, 27, 31 };
				
		// Set up system ready for test
		Application app = new Application(appWithQueriesName, appWithQueriesPropertiesFilePath);
		CodeMethod method = app.getClassByName(expectedClassName).getMethodByName(expectedMethodName);
		
		method.getQueryByStartingLineNumber(28);
		fail("QueryWithGivenStartNumberNotFoundException expected");
	}
	
	
	// Helper methods for the test cases
	
	// Convert a list of Statements into a List of their line numbers
	private Integer[] getLineNumbersForAllQueryStatements(Query query) throws DFQException {
		List<? extends Statement> stmts = query.getStatements();
		return Lists.newArrayList(Lists.transform(stmts, new Function<Statement, Integer>() {
			public Integer apply(Statement stmt) {
				return stmt.getLineNumber();
			}
		})).toArray(new Integer[0]);
	}
}
