package cz.dp.share.exceptions;

public class RecordNotFoundException extends BaseException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RecordNotFoundException(String message)
	{
		super(message);
	}
	
	public RecordNotFoundException(String message, String podrobnosti)
	{
		super(message, podrobnosti);
	}
	
	public RecordNotFoundException(String message, String podrobnosti, boolean nonFatal)
	{
		super(message, podrobnosti);
		setNonFatal(nonFatal);
	}
}
