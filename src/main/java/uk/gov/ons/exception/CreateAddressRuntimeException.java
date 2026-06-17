package uk.gov.ons.exception;

import java.io.Serial;

public class CreateAddressRuntimeException extends RuntimeException {
	@Serial
    private static final long serialVersionUID = 1L;

	public CreateAddressRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateAddressRuntimeException(Throwable cause) {
		super(cause);
	}

	public CreateAddressRuntimeException(String message) {
		super(message);
	}	
}
