package uk.gov.ons.exception;

public class CreateAddressException extends Exception {
	private static final long serialVersionUID = 1L;

	public CreateAddressException() {
		super();
	}

	public CreateAddressException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CreateAddressException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateAddressException(String message) {
		super(message);
	}

	public CreateAddressException(Throwable cause) {
		super(cause);
	}
}
