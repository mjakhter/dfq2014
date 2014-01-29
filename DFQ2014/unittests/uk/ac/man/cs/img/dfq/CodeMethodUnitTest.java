package uk.ac.man.cs.img.dfq;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/** @author Javid Akhter
 * Unit Tests for CodeMethod class
 * 
 */

public class CodeMethodUnitTest {

	private String appName = "testOnePassOneFail";
	private String propertiesFilePath = DFQTest.getPropertiesFile(appName);
	

	CodeClass codeClass;
	Application app;

	@Before
	public void setUp() throws Exception {
		this.app = new Application(appName, propertiesFilePath);
		this.codeClass = this.app.getClassByName("Game");
	}

	@Test
	public void testCodeMethod() {
		String expMethodName = "getPlayerList";
		String actualMethodName = codeClass.getMethods().get(0).getName();

		assertEquals(expMethodName, actualMethodName);
	}

	@Test
	public void testGetName() {
		String expMethodName = "getGameType";
		String actualMethodName = codeClass.getMethods().get(1).getName();

		assertEquals(expMethodName, actualMethodName);
	}

	@Test
	public void testGetStatementByLineNumber() throws DFQException {
		int lineNumber = 12;
		String expectedStatement = "setPlayerList(playerList);";
		Statement actualStatement = 
				codeClass.getMethods().get(0).getStatementByLineNumber(lineNumber);

		assertEquals(expectedStatement, actualStatement.getStatementText());
	}

	@Test
	public void testGetSignature() {
		String expSignature = "getGameType()I";
		String actualSignature = codeClass.getMethods().get(1).getSignature();

		assertEquals(expSignature, actualSignature);
	}

	@Test
	public void testGetStatements() throws DFQException {
		int lineNumber = 29;
		String expectedStatement = "this.playerList = playerList;";
		Statement actualStatement = 
				codeClass.getMethods().get(0).getStatementByLineNumber(lineNumber);

		assertEquals(expectedStatement, actualStatement.getStatementText());
	}
	
	@Test
	public void testGetStatementsWithWeirdSwapLines() throws Exception {
		int lineNumber = 15;
		String stmtText = "Player player2 = new Player(\"Joan\");";
		
		CodeClass codeClass = this.app.getClassByName("GameTest");
		CodeMethod method = codeClass.getMethodByName("testGameResigned");
		
		Statement stmt = method.getStatementByLineNumber(lineNumber);
		
		Map<Integer, Statement> allStmt = method.getStatements();
		Statement stmt2 = allStmt.get(lineNumber);
		
		assertEquals(lineNumber, stmt.getLineNumber());
		assertEquals(stmtText, stmt.getStatementText());
		
		assertEquals(lineNumber, stmt2.getLineNumber());
		assertEquals(stmtText, stmt2.getStatementText());
		
		assertEquals(stmt.getLineNumber(), stmt2.getLineNumber());
		assertEquals(stmt.getStatementText(), stmt2.getStatementText());
		
	}
}
