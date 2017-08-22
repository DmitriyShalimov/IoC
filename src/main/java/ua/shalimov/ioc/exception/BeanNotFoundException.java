package ua.shalimov.ioc.exception;

public class BeanNotFoundException extends RuntimeException {
    public BeanNotFoundException(String massage) {
        super(massage);
    }
}
