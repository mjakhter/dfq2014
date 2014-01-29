package uk.ac.man.cs.img.dfq;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * @author Javid Akhter
 * @author Suzanne M. Embury
 * 
 * The instances of this class represent the classes in the application being diagnosed.
 */
public class CodeClass {

	public static final String JAVA_CLASS_FILE_SUFFIX = ".java";
	public static final String CONSTRUCTOR_NAME_IN_TRACE_FILE = "<init>";

	private String name;
	private String sourceFilePath;

	private List<CodeMethod> codeMethods = new ArrayList<CodeMethod>();

	// Constructor
	public CodeClass(String sourceClassPath) throws DFQException {
		this.sourceFilePath = sourceClassPath;
		this.name = extractClassNameFromSourceFilePath();

		createMethods();
	}

	private String extractClassNameFromSourceFilePath() {
		int index = this.sourceFilePath.lastIndexOf(File.separator);
		return this.sourceFilePath.substring(index+1, this.sourceFilePath.length() - 
				CodeClass.JAVA_CLASS_FILE_SUFFIX.length());
	}

	private void createMethods() throws DFQException {
		ApplicationTrace trace = Application.currentApplication().getTrace();			
		List<String> methodSignatures = trace.getProductionMethodSignaturesFromTrace(this.name);
		for (String methodSignature: methodSignatures) {
			String methodName = methodSignature.substring(0, methodSignature.indexOf('('));
			if(methodName.equals(CONSTRUCTOR_NAME_IN_TRACE_FILE)) {
				methodName = methodName.replace(CONSTRUCTOR_NAME_IN_TRACE_FILE, this.name);
			}
			CodeMethod codeMethod = new CodeMethod(methodName, methodSignature, this);	
			codeMethods.add(codeMethod);	
		}		
	}


	// Getters and setters
	public String getName() {
		return this.name;
	}

	public String getFilePath() {
		return this.sourceFilePath;
	}

	public List<CodeMethod> getMethods() {
		return this.codeMethods;                             
	}

	public CodeMethod getMethodByName(String methodName) throws MethodWithGivenNameNotFoundException {
		for (CodeMethod codeMethod: this.codeMethods) {
			if (codeMethod.getName().matches(methodName)){
				return codeMethod;
			}
		}
		throw new MethodWithGivenNameNotFoundException();
	}

	public CodeMethod getMethodBySignature(String expectedSignature)  throws MethodWithGivenSignatureNotFoundException {

		for (CodeMethod methodSignature: this.codeMethods) {
			if (methodSignature.getSignature().equals(expectedSignature)) {
				return methodSignature;
			}
		}
		throw new MethodWithGivenSignatureNotFoundException();
	}

	public Statement getStatementByLineNumber(int lineNumber) throws DFQException {
		for (CodeMethod codeMethod: this.codeMethods) {
			if (lineNumber >= codeMethod.getStartLineNumber() && lineNumber <= codeMethod.getEndLineNumber())
				return codeMethod.getStatementByLineNumber(lineNumber);
		}
		throw new StatementWithGivenClassAndLineNumberNotFoundException();		
	}

}