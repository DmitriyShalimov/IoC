package ua.shalimov.ioc.testData;


public class UserService {
    private MailService mailService;

    public UserService() {
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void sendEmailWithUsersCount() {
        int numberOfUsersInSystem = getUsersCount();
        mailService.sendEmail("tech@progect.com", "there are " + numberOfUsersInSystem + " users in system!");
    }

    private int getUsersCount() {
        return (int) (Math.random() * 1000);
    }

}
