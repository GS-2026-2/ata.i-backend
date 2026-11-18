package br.com.atai.exception;

/**
 * Exceção lançada quando há conflito de unicidade ou estado exclusivo do recurso (HTTP 409).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
