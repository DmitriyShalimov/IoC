package ua.shalimov.ioc.exception;

public class NoUniqueBeanException extends RuntimeException {
    public NoUniqueBeanException(String massage) {
        super(massage);
    }
}
