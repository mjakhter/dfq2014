package uk.ac.man.cs.img.dfq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * @author Javid Akhter
 * @author Suzanne M. Embury
 * 
 * The instances of this class represent the methods in the application being diagnosed.
 */

public class CodeMethod {

	private String name;
	private String methodSignature;
	private CodeClass parentClass;
	
	private Map<Integer, Statement> statements = new HashMap<Integer, Statement>(); 
	private List<Query> queries = new ArrayList<Query>();

	private int startLineNumber;
	private int endLineNumber;
	
		
	// Constructor 
	public CodeMethod(String name, String methodSignature, CodeClass parentClass) throws DFQException {
		this.name = name;
		this.methodSignature = methodSignature;
		this.parentClass = parentClass;
		this.startLineNumber = getStartLineNumberFromTraceFile();
		this.endLineNumber = getEndLineNumberFromTraceFile();	
		
		createStatements();		
	}

	private int getStartLineNumberFromTraceFile() throws DFQException {
		int startLine = Application.currentApplication().getTrace().getMethodStartLine(this.methodSignature);
		
		if (this.isConstructor())
			startLine++;
		return startLine;
	}
	
	private int getEndLineNumberFromTraceFile() throws DFQException {
		int endLine = Application.currentApplication().getTrace().getMethodEndLine(this.methodSignature);
		if (ApplicationTrace.isSignatureOfVoidMethod(this.methodSignature))
			endLine--;
		return endLine;
	}
		
	private void createStatements() throws DFQException {
		String classSourcePath = this.parentClass.getFilePath();
		File sourceCodeFile = new File(classSourcePath);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(sourceCodeFile));
		} catch (FileNotFoundException e) {
			throw new ClassFileNotFoundException(classSourcePath);
		}
		
		extractedStatements(classSourcePath, br);
		extractQueries();
	}


	private void extractedStatements(String classSourcePath, BufferedReader br) throws DFQException {
		LineNumberReader lnr = new LineNumberReader(br);
		int lineNumber = 0;
		String currentLine = tryToReadNextLine(lnr, classSourcePath, lineNumber);
		while (currentLine != null) {
			if (lineNumber >= this.startLineNumber && lineNumber <= this.endLineNumber) {
				Statement statement = new Statement(this, lineNumber, currentLine);
				statements.put(lineNumber, statement);
			}
			currentLine = tryToReadNextLine(lnr, classSourcePath, lineNumber++);
		}		
	}

	private String tryToReadNextLine(LineNumberReader lnr, String classSourcePath, int lineNumber) throws CouldNotReadFromFileException {
		try {
			return lnr.readLine();
		} catch (IOException e) {
			throw new CouldNotReadFromFileException("file " + classSourcePath + " at line " + lineNumber);
		}
	}
	
	private void extractQueries() throws DFQException {
		for (Statement stmt : statements.values()) {
			if (isTheStatementAQuery(stmt)) {
				List<Statement> queryStmts = new ArrayList<Statement>();
				queryStmts.add(stmt);
				Query query = new Query(this, queryStmts);
				queries.add(query);
			} 
		}
	}


	private boolean isTheStatementAQuery(Statement stmt) {
		if(stmt.getStatementText().contains("whereClause = \"forename")
				|| stmt.getStatementText().contains("whereClause += \"and surname")
				|| stmt.getStatementText().contains("list()")){
			return true;
		} else if (stmt.getStatementText().contains("list()")
				|| stmt.getStatementText().contains("(mobileMatches)")) {
			return true;
		} 
		return false;
	}

	// Getter and setters

	public String getName() {
		return this.name;
	}
	
	public CodeClass getParentClass() {
		return this.parentClass;
	}

	public String getSignature() {
		return this.methodSignature;
	}
	
	public int getStartLineNumber() {
		return startLineNumber;
	}

	public int getEndLineNumber() {
		return endLineNumber;
	}
	
	public boolean isConstructor() {
		return this.getSignature().startsWith(ApplicationTrace.CONSTRUCTOR_NAME_IN_TRACE_FILE);
	}
	
		
	public Statement getStatementByLineNumber(int lineNumber) throws DFQException {
		if (lineNumber < this.startLineNumber || lineNumber > this.endLineNumber ) 
			throw new AttemptToAccessStatementOutwithGivenMethodException();

		for (Map.Entry<Integer, Statement> singleKeyValuePair : statements.entrySet()) {
			Statement statementText = singleKeyValuePair.getValue();
			if (singleKeyValuePair.getKey() == lineNumber) {
				return statementText;		
			}
		}
		throw new StatementWithGivenLineNumberNotFoundException();
	}
	
	public Map<Integer, Statement> getStatements() throws DFQException { 
		return this.statements;
	}

	public List<Statement> getExecutedStatements() throws DFQException {
		ApplicationTrace trace = Application.currentApplication().getTrace();
		List<Statement> execStmts = trace.getExecutedStatementsByMethod(name);  
		return execStmts;
	}

	public List<Statement> getTraceOfExecution() throws DFQException {
		ApplicationTrace trace = Application.currentApplication().getTrace();
		List<Statement> execStmts = trace.getExecutedStatementsByMethod(name);  
		return execStmts;
	}

	public List<Query> getExecutedQueries() throws DFQException {
		//TO DO: This method needs to be revisited 
		return null;
	}

	
	public Query getQueryByStartingLineNumber(int lineNum) throws DFQException {
		for(Query query:queries) {
			Statement queryLine = query.getMethod().getStatementByLineNumber(lineNum);
			List<Statement> queryStmts = new ArrayList<Statement>();
			queryStmts.add(queryLine);
			Query queryByLineNum = new Query(this, queryStmts);
			return queryByLineNum;
		}
		throw new QueryWithGivenLineNumberNotFoundException();		
	}

	public List<Query> getQueries() throws DFQException {
		return this.queries;
	}

}