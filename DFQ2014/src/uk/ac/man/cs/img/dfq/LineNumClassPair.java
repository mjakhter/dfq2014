package uk.ac.man.cs.img.dfq;

/*
 * @author Javid Akhter
 * @author Suzanne M. Embury
 * 
 */

public class LineNumClassPair<String> {         
	
	private int lineNum; 
	private String className;

	
	//Constructor	
	public LineNumClassPair(int line, String className) {    
		this.lineNum = line;
		this.className = className;
	}
	
	// Getters
	public int getLineNum() {
		return lineNum;
	}

	public String getClassName() {
		return className;
	}
}