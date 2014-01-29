package uk.ac.man.cs.img.dfq;

/*
 * @author Javid Akhter
 * @author Suzanne M. Embury
 * 
 * The instances of this class represent individual code statements in the application being diagnosed.
 */

public class Statement { 

	private CodeMethod codeMethod;
	private int lineNumber;
	private String statementText;

	// Constructor
	public Statement(CodeMethod codeMethod, int lineNumber, String statementText) throws LineNumberNotFoundException {
		this.codeMethod = codeMethod;
		this.lineNumber = lineNumber;
		this.statementText = statementText.trim();
	}
	
	// Getters

	public CodeMethod getCodeMethod() {
		return this.codeMethod;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}

	public String getStatementText() {
		return this.statementText;
	}
	
	public String toString() {
		return this.getCodeMethod().getParentClass().getName() + "." + this.getCodeMethod().getName() + "() line " + this.lineNumber;
	}

}
