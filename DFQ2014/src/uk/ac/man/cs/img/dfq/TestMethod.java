package uk.ac.man.cs.img.dfq;




/* @author Javid Akhter
 * @author Suzanne M. Embury
 * 
 * The instances of this class represent the test methods in the application under test.
 */

public class TestMethod extends CodeMethod {

	// Constructor
	
	public TestMethod(String name, String signature, CodeClass parentClass) throws DFQException {
		super(name, signature, parentClass);
	}
	
	public boolean testPassed() throws DFQException {
		return Application.currentApplication().getTrace().isPassing(this);
	}	
}