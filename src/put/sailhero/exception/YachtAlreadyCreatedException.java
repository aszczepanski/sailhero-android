package put.sailhero.exception;

public class YachtAlreadyCreatedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public YachtAlreadyCreatedException() {
		super();
	}

	public YachtAlreadyCreatedException(String message) {
		super(message);
	}
}
