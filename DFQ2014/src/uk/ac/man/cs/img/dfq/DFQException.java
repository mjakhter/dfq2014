package uk.ac.man.cs.img.dfq;

public class DFQException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public DFQException() {
		
	}
	
	public DFQException(String query) {
		super(query);
	}

}
