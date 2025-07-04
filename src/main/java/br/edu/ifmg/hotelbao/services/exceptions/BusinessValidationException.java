package br.edu.ifmg.hotelbao.services.exceptions;

public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException() {
        super();
    }

    public BusinessValidationException(String message) {
        super(message);
    }

}
