package cz.diamo.share.exceptions;

public class ValidationException extends BaseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String message, String podrobnosti) {
		super(message, podrobnosti);
	}

	public ValidationException(String message, String podrobnosti, boolean nonFatal) {
		super(message, podrobnosti);
		setNonFatal(nonFatal);
	}
}
