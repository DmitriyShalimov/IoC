package ua.shalimov.ioc.context;

import org.junit.Before;
import org.junit.Test;
import ua.shalimov.ioc.exception.BeanNotFoundException;
import ua.shalimov.ioc.testData.MailService;
import ua.shalimov.ioc.testData.PaymentService;
import ua.shalimov.ioc.testData.UserService;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.*;

public class ApplicationContextTest {

    private ApplicationContext applicationContext;

    @Before
    public void setUpApplicationContext() {
        applicationContext = new ClassPathApplicationContext("src/main/resources/context.xml");
    }

    @Test
    public void getBeanForNameTest() {
        assertThat(applicationContext.getBean("mailService").getClass().getSimpleName(), is("MailService"));
        assertThat(applicationContext.getBean("userService").getClass().getSimpleName(), is("UserService"));
        assertThat(applicationContext.getBean("paymentService").getClass().getSimpleName(), is("PaymentService"));
    }

    @Test
    public void getBeanForNameAndClassTest() {
        PaymentService paymentService = applicationContext.getBean("paymentService", PaymentService.class);
        assertThat(paymentService.getClass().getSimpleName(), is("PaymentService"));
        assertThat(paymentService.getMaxAmount(), is(0));
        paymentService = applicationContext.getBean("paymentWithMaxService", PaymentService.class);
        assertThat(paymentService.getMaxAmount(), is(5000));
    }

    @Test
    public void getBeanForClassTest() {
        assertThat(applicationContext.getBean(MailService.class).getClass().getSimpleName(), is("MailService"));
        assertThat(applicationContext.getBean(UserService.class).getClass().getSimpleName(), is("UserService"));
        assertThat(applicationContext.getBean(PaymentService.class).getClass().getSimpleName(), is("PaymentService"));
    }

    @Test
    public void getBeanNameTest() {
        List beanNames = applicationContext.getBeanName();
        assertThat(beanNames.size(), is(4));
        assertThat(beanNames.contains("paymentService"), is(true));
        assertThat(beanNames.contains("paymentWithMaxService"), is(true));
        assertThat(beanNames.contains("userService"), is(true));
        assertThat(beanNames.contains("mailService"), is(true));
    }

    @Test(expected = BeanNotFoundException.class)
    public void getBeanForClassExceptionTest() {
        applicationContext.getBean(String.class);
    }

    @Test(expected = BeanNotFoundException.class)
    public void getBeanForNameExceptionTest() {
        applicationContext.getBean("service");
    }

    @Test(expected = BeanNotFoundException.class)
    public void getBeanForNameAndClassExceptionTest() {
        applicationContext.getBean("service", String.class);
    }
}
