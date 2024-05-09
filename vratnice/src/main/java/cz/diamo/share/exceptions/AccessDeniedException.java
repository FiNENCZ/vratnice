package cz.diamo.share.exceptions;

public class AccessDeniedException extends BaseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AccessDeniedException(String message) {
		super(message);
	}

	public AccessDeniedException(String message, String podrobnosti) {
		super(message, podrobnosti);
	}

	public AccessDeniedException(String message, String podrobnosti, boolean nonFatal) {
		super(message, podrobnosti);
		setNonFatal(nonFatal);
	}
}
