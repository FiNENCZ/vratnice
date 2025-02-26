package cz.dp.share.exceptions;

public class UniqueValueException extends BaseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UniqueValueException(String message) {
		super(message);
	}

	public UniqueValueException(String message, String podrobnosti) {
		super(message, podrobnosti);
	}

	public UniqueValueException(String message, String podrobnosti, boolean nonFatal) {
		super(message, podrobnosti);
		setNonFatal(nonFatal);
	}
}
