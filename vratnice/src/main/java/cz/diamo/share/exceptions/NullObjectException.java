package cz.diamo.share.exceptions;

public class NullObjectException extends BaseException  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NullObjectException(String message)
	{
		super(message);
	}
	
	public NullObjectException(String message, String podrobnosti)
	{
		super(message, podrobnosti);
	}
	
	public NullObjectException(String message, String podrobnosti, boolean nonFatal)
	{
		super(message, podrobnosti);
		setNonFatal(nonFatal);
	}
}
