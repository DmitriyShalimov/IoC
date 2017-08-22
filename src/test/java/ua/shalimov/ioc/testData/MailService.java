package ua.shalimov.ioc.testData;


public class MailService {
    private String protocol;
    private int port;

    public MailService() {
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void sendEmail(String emailTo, String content) {
        System.out.println("Sending email to: "+emailTo);
        System.out.println("With content: "+content);
    }
}
