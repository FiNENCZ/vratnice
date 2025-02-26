package cz.dp.share.exceptions;

public class DialogRequireException extends BaseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DialogRequireException(String message) {
		super(message);
	}

	public DialogRequireException(String message, String podrobnosti) {
		super(message, podrobnosti);
	}

	public DialogRequireException(String message, String podrobnosti, boolean nonFatal) {
		super(message, podrobnosti);
		setNonFatal(nonFatal);
	}
}
