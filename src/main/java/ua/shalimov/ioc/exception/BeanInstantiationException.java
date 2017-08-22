package ua.shalimov.ioc.exception;

public class BeanInstantiationException extends RuntimeException {
    public BeanInstantiationException(String massage) {
        super(massage);
    }

    public BeanInstantiationException(Exception e) {
        super(e);
    }
}
