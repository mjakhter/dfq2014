package uk.ac.man.cs.img.dfq;

import java.util.List;

/*
 * @author Javid Akhter
 * @author Suzanne M. Embury
 * 
 * The instances of this class represent test classes in the application being diagnosed.
 */
public class TestClass extends CodeClass {

	// Constructor

	public TestClass(String sourceClassPath) throws DFQException {
		super(sourceClassPath);

		ApplicationTrace trace = Application.currentApplication().getTrace();			
		List<String> methodSignatures = trace.getTestMethodSignaturesFromTrace(this.getName());
		for (String methodSignature: methodSignatures) {
			String methodName = methodSignature.substring(0, methodSignature.indexOf('('));
			if ("<init>".equals(methodName)) { 
				//TODO add as a constructor
			} else {
				CodeMethod testMethod = new TestMethod(methodName, methodSignature, this);
				this.getMethods().add(testMethod);
			}
		}
	}

}