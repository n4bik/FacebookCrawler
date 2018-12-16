package pl.tomaszbuga.crawler;

import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.MessagingException;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import java.util.Date;
import java.util.Properties;

class HtmlEmailSender {
    static final EmailHostConfig GMAIL_CONFIG = new EmailHostConfig("smtp.gmail.com", "587");

    private Properties properties;
    private Session session;
    private InternetAddress fromAddress;

    HtmlEmailSender(EmailHostConfig emailHostConfig, ImmutablePair<String, String> userNamePassword) throws AddressException {
        createProperties(emailHostConfig);
        createSession(userNamePassword);
        createFromAddress(userNamePassword);
    }

    private void createFromAddress(ImmutablePair<String, String> userNamePassword) throws AddressException {
        fromAddress = new InternetAddress(userNamePassword.left);
    }

    private void createSession(final ImmutablePair<String, String> userNamePassword) {
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userNamePassword.left, userNamePassword.right);
            }
        };

        session = Session.getInstance(properties, auth);
    }

    private void createProperties(EmailHostConfig emailHostConfig) {
        properties = new Properties();
        properties.put("mail.smtp.host", emailHostConfig.host);
        properties.put("mail.smtp.port", emailHostConfig.port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
    }

    void sendHtmlEmailWithAttachment(String toAddress, String subject, String message, String filepath, String filename) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(fromAddress);
        InternetAddress[] toAddresses = {new InternetAddress(toAddress)};
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        BodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setContent(message, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();

        multipart.addBodyPart(messageBodyPart);

        messageBodyPart = new MimeBodyPart();
        FileDataSource source = new FileDataSource(filepath);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);

        msg.setContent(multipart);

        Transport.send(msg);
    }

    @Data
    public static class EmailHostConfig {
        private final String host;
        private final String port;

        EmailHostConfig(String host, String port) {
            this.host = host;
            this.port = port;
        }
    }
}

