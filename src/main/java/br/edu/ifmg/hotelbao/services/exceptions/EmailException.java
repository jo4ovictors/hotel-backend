package br.edu.ifmg.hotelbao.services.exceptions;

public class EmailException extends RuntimeException {

    public EmailException() {
        super();
    }

    public EmailException(String message) {
        super(message);
    }
}
