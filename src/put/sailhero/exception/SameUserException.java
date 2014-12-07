package put.sailhero.exception;

public class SameUserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SameUserException() {
		super();
	}

	public SameUserException(String message) {
		super(message);
	}
}
