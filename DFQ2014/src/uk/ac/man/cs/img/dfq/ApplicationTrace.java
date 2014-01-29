package uk.ac.man.cs.img.dfq;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;
import net.sf.saxon.s9api.XdmValue;

import org.xml.sax.InputSource;

/*
 * @author Javid Akhter
 * @author Suzanne Embury
 * 
 * The instances of this class represent the outcomes of a single execution of the 
 * application being diagnosed.
 */

public class ApplicationTrace {

	public static final String CONSTRUCTOR_NAME_IN_TRACE_FILE = "<init>";
	private static final String NAMESPACE_DECLARATION = "declare namespace ns0=\"http://www.cc.gatech.edu/aristotle/2008/tarantula\"; ";
	private static final String SIGNATURE_SUFFIX_OF_VOID_METHODS = ")V";
	
	private String traceFilePath;

	// Constructor
	public ApplicationTrace(String xmlFilePath) throws DFQException {
		this.traceFilePath = xmlFilePath;
	}

	// Getters and Setters

	public String getTraceFilePath() {
		return this.traceFilePath;
	}
	
	public static boolean isSignatureOfVoidMethod(String methodSignature) {
		return methodSignature.endsWith(SIGNATURE_SUFFIX_OF_VOID_METHODS);
	}

	public boolean isPassing(CodeMethod testMethod) throws DFQException {
		List<String> testResult = getTestResultFromTrace(testMethod.getName());
		if (testResult.size() > 0) {
			String passing = testResult.get(0);
			return Boolean.parseBoolean(passing);
		} else   
			throw new CouldNotFindTestResultInTraceException();
	}

	public List<TestMethod> getExecutedTests() throws Exception {	
		List<String> testsName = this.getExecutedTestsFromTrace();
		List<TestMethod> testMethods = new ArrayList<TestMethod>(); 
		for (String testName: testsName) {		
			TestMethod testMethod = convertNameToTestMethod(testName);
			testMethods.add(testMethod);
		}
		return testMethods;
	}

	private TestMethod convertNameToTestMethod(String methodName) throws DFQException { 
		List<TestClass> testClasses = Application.currentApplication().getTestClasses();		
		for (TestClass  testClass: testClasses) {
			CodeMethod codeMethod = testClass.getMethodByName(methodName);
			return (TestMethod) codeMethod;
		}
		throw new MethodWithGivenNameNotFoundException();
	}

	public List<TestMethod> getPassedTests() throws DFQException {
		List<String> passedTestsName = this.getPassingTestsFromTraceFile();
		List<TestMethod> passedTestMethods = new ArrayList<TestMethod>();	
		for (String passedTestName: passedTestsName) {  
			TestMethod testMethod = convertNameToTestMethod(passedTestName);
			passedTestMethods.add(testMethod);
		}
		return passedTestMethods;
	}

	public List<TestMethod> getFailedTests() throws DFQException {
		List<String> failedTestsName = this.getFailingTestsFromTraceFile();		
		List<TestMethod> failedTestMethods = new ArrayList<TestMethod>();		
		for (String failedTestName: failedTestsName) {  
			TestMethod testMethod = convertNameToTestMethod(failedTestName);
			failedTestMethods.add(testMethod);
		}
		return failedTestMethods;		
	}

	public Map<TestMethod, List<Query>> identifyAllCandidateQueries() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Statement> getExecutedStatementsByMethod(String methodName) throws DFQException {
		List<LineNumClassPair<String>> execStmts = this.getExecutedStatementsFromTrace(methodName);
		List<Statement> statements = new ArrayList<Statement>();
		for (LineNumClassPair<String> stmt:execStmts) {
			Statement stmts = convToStatement(stmt);
			if (stmts != null) {
				statements.add(stmts);
			}
		}
		return statements;
	}
	
	private Statement convToStatement(LineNumClassPair<String> stmt) throws DFQException {		
		Application app = Application.currentApplication();
		CodeClass className = app.getClassByName(stmt.getClassName());
		List<CodeMethod> methods = className.getMethods();
		Statement statement = null;
		for (CodeMethod method:methods) {
			if(stmt.getLineNum() >= method.getStartLineNumber() && stmt.getLineNum() <= method.getEndLineNumber()) {
				statement = className.getStatementByLineNumber(stmt.getLineNum());
			}
		}
		return statement;		
	}
	
//	public List<Query> getAllExecutedQueriesFromTrace(String methodName) throws DFQException {
//		List<Statement> stmts = this.getExecutedStatementsByMethod(methodName);
//		List<Query> conQueries = new ArrayList<Query>();
//		for (Statement stmt:stmts) {
//			if (stmt.getStatementText().contains("session")) {
//				Query query = convToQuery(stmt);
//				conQueries.add(query);
//			}
//		}
//		return conQueries;
//	}
//
//	private Query convToQuery(Statement statement) throws DFQException {
//		Application app = Application.currentApplication();
//		CodeClass parentClass = statement.getCodeMethod().getParentClass();
//		CodeClass className = app.getClassByName(parentClass.getName());
//		List<CodeMethod> methods = className.getMethods();
//		Query query = new Query();
//		for (CodeMethod method:methods) {
//			if(statement.getLineNumber() >= method.getStartLineNumber() && statement.getLineNumber() <= method.getEndLineNumber()) {
//				Statement stm = className.getStatementByLineNumber(statement.getLineNumber());
//				if (stm.getStatementText().contains("session")) {
//	//				query = stm.getStatementText();
//				}
//			}
//		}
//		return query;
//	}

	

	// Utility Methods for Extracting Data from the XML Trace File

	public int getMethodStartLine(String methodSignature) throws DFQException {				
		String startLine = NAMESPACE_DECLARATION +
				"for $class in .//ns0:file/ns0:class " +
				"  for $method in $class/ns0:method " +
				"    for $line in $method/ns0:line[@type='start'] " +
				"where $method/ns0:signature = \"" + methodSignature + "\" " + 
				"return <line>{$line/text()}</line> ";
		List<String> origStartLine = processXQuery(startLine);	
		if (origStartLine.size() > 0) {
			String convStartLine = origStartLine.get(0);
			int startLineNumber = Integer.parseInt(convStartLine);
			return startLineNumber;
		}
		throw new CouldNotFindStartLineNumberForGivenMethodException();
	}

	public int getMethodEndLine(String methodSignature) throws DFQException {				 
		String endLine = NAMESPACE_DECLARATION +
				"for $class in .//ns0:file/ns0:class " +
				"  for $method in $class/ns0:method " +
				"    for $line in $method/ns0:line[@type='end'] " +
				"where $method/ns0:signature = \"" + methodSignature + "\" " + 
				"return <line>{$line/text()}</line> ";
		List<String> origEndLine = processXQuery(endLine);		
		if (origEndLine.size() > 0) {
			String convEndLine = origEndLine.get(0);
			int endLineNumber = Integer.parseInt(convEndLine);
			return endLineNumber;
		}
		throw new CouldNotFindEndLineNumberForGivenMethodException();
	}

	private List<String> getExecutedTestsFromTrace() throws DFQException {
		String query = NAMESPACE_DECLARATION + 
				"for $tc in .//ns0:test_case " + 
				"return <x>{data($tc/@name)}</x>";

		return processXQuery(query);		
	}	

	public List<String> getProductionMethodSignaturesFromTrace(String className) throws DFQException {				
		String query = NAMESPACE_DECLARATION +
				"for $class in .//ns0:program/ns0:file/ns0:class " +
				"  for $method in $class/ns0:method " +
				"where $class/ns0:name = \"" + className + "\" " +
				"return $method/ns0:signature/text() ";
		return processXQuery(query);
	}
	
	public List<String> getTestMethodSignaturesFromTrace(String className) throws DFQException {				
		String query = NAMESPACE_DECLARATION +
				"for $class in .//ns0:test_suite/ns0:file/ns0:class " +
				"  for $method in $class/ns0:method " +
				"where $class/ns0:name = \"" + className + "\" " +
				"return $method/ns0:signature/text() ";
		return processXQuery(query);
	}
	

	private List<String> getTestResultFromTrace(String testName) throws DFQException {
		String query = NAMESPACE_DECLARATION +
				"for $tc in .//ns0:test_case[@name=\"" + testName + "\"] " +
				"return <x>{data($tc/@passing)}</x> ";
		return processXQuery(query);
	}


	private List<String> getPassingTestsFromTraceFile() throws DFQException {
		String query = NAMESPACE_DECLARATION +
				"for $tc in .//ns0:test_case[@passing=\"true\"] " +
				"return <x>{data($tc/@name)}</x> ";
		return processXQuery(query);
	}

	private List<String> getFailingTestsFromTraceFile() throws DFQException {
		String query = NAMESPACE_DECLARATION +
				"for $tc in .//ns0:test_case[@passing=\"false\"] " + 
				"return <x>{data($tc/@name)}</x> ";
		return processXQuery(query);
	}

	
	private List<LineNumClassPair<String>> getExecutedStatementsFromTrace(String methodName) throws DFQException {
		String query = NAMESPACE_DECLARATION +		 
				" for $tc in .//ns0:test_case " +
				" for $stm in $tc/ns0:statement " +
				" for $fstmt in .//ns0:file/ns0:statement " +
					"where $tc/@name = \"" + methodName + "\" and $stm/ns0:id/text() = $fstmt/@id " +
				"return <res><ln>{$fstmt/ns0:line/text()}</ln> "+ 
					"<cn>{$fstmt/../ns0:class/ns0:name/text()}</cn></res> "; 
		return processXQueryForLineNumAndClass(query);
	}
	
	
	// Utility Methods for Evaluating XQueries
	
	private List<LineNumClassPair<String>> processXQueryForLineNumAndClass(String query) throws DFQException {
		Processor processor = new Processor(false);
		XQueryCompiler xqc = processor.newXQueryCompiler();
		XQueryExecutable xqex;
		try {
			xqex = xqc.compile(query);
		} catch (SaxonApiException e) {
			throw new CouldNotCompileXQueryExpression(e.getMessage() + " with query \"" + query + "\"");
		}

		XQueryEvaluator xqev = xqex.load();
		InputSource is = new InputSource((new File(traceFilePath).toURI().toString()));
		SAXSource source = new SAXSource(is);
		try {
			xqev.setSource(source);
		} catch (SaxonApiException e) {
			throw new CouldNotSetSourceForXQueryEvaluator(e.getMessage() + " with path \"" + traceFilePath + "\"");
		}

		XdmValue queryResult;
		try {
			queryResult = xqev.evaluate();
		} catch (SaxonApiException e) {
			throw new CouldNotEvaluateXQueryExpression(e.getMessage() + " with query \"" + query + "\"");
		}

		List<LineNumClassPair<String>> stmtsPair = new ArrayList<LineNumClassPair<String>>(); 
		for (XdmItem item : queryResult) {
			XdmNode res = (XdmNode) item;
			XdmNode lnNode = getChild(res, "ln");
			String ln = lnNode.getStringValue();
			int lnNum = Integer.parseInt(ln);
			XdmNode cnNode = getChild(res, "cn");            
			String cn = cnNode.getStringValue();
			stmtsPair.add(new LineNumClassPair<String>(lnNum, cn));
		}
		return stmtsPair;
	}
		
	private static XdmNode getChild(XdmNode parent, String childName) {
		XdmSequenceIterator iter = parent.axisIterator(Axis.CHILD, new QName(childName));
		if (iter.hasNext()) {
			return (XdmNode)iter.next();
		} else {
			return null;
		}
	}
	

	private List<String> processXQuery(String query) throws DFQException {
		Processor processor = new Processor(false);
		XQueryCompiler xqc = processor.newXQueryCompiler();
		XQueryExecutable xqex;
		try {
			xqex = xqc.compile(query);
		} catch (SaxonApiException e) {
			throw new CouldNotCompileXQueryExpression(e.getMessage() + " with query \"" + query + "\"");
		}

		XQueryEvaluator xqev = xqex.load();
		InputSource is = new InputSource((new File(traceFilePath).toURI().toString()));
		SAXSource source = new SAXSource(is);
		try {
			xqev.setSource(source);
		} catch (SaxonApiException e) {
			throw new CouldNotSetSourceForXQueryEvaluator(e.getMessage() + " with path \"" + traceFilePath + "\"");
		}

		XdmValue queryResult;
		try {
			queryResult = xqev.evaluate();
		} catch (SaxonApiException e) {
			throw new CouldNotEvaluateXQueryExpression(e.getMessage() + " with query \"" + query + "\"");
		}

		List<String> resultList = new ArrayList<String>();
		for (XdmItem item : queryResult) {
			resultList.add(item.getStringValue());
			
		}
		return resultList;
	}

}