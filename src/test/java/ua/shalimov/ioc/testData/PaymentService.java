package ua.shalimov.ioc.testData;

public class PaymentService {
    private MailService mailService;
    private int maxAmount;

    public MailService getMailService() {
        return mailService;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void pay(String from, String to, double amount) {
        mailService.sendEmail("from", "payment successful");
        mailService.sendEmail("to", "payment successful");
    }

}
