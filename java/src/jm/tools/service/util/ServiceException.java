package jm.tools.service.util;

public class ServiceException extends Exception {
	private static final long serialVersionUID = 6703786130943548756L;

	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message);
		ExceptionUtils.initCause(this, cause);
	}
}
