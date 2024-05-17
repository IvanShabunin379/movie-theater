package edu.database.exception;

public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(Throwable reason) {
        super(reason);
    }
}
