package edu.domain.repository.exception;

public class DataAccessException extends RuntimeException {
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
