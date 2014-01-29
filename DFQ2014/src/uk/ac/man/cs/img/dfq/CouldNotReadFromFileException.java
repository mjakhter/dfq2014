package uk.ac.man.cs.img.dfq;

public class CouldNotReadFromFileException extends DFQException {

	private static final long serialVersionUID = 1L;

	public CouldNotReadFromFileException(String query) {
		super(query);
	}

}
