package uk.ac.man.cs.img.dfq;

import java.util.List;

public class Query {

	private CodeMethod 		codeMethod;
	private List<Statement> statements;
	
	/**
	 * @param codeMethod
	 * @param statements
	 */
	public Query(CodeMethod codeMethod, List<Statement> statements) {
		super();
		this.codeMethod = codeMethod;
		this.statements = statements;
	}

	public CodeMethod getMethod() {
		return this.codeMethod;
	}

	public void uniqueResult() {
		// TODO Auto-generated method stub
	}
	
	public String getClassName() {
		return getMethod().getParentClass().getName();
	}

	public String getMethodName() {
		return getMethod().getName();
	}

	public List<Statement> getStatements() throws DFQException {
		return this.statements;
	}

	public Query getSuspiciousnessScore() {
		// TODO Auto-generated method stub
		return null;
	}

}
