package dnf.module.auction.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class JMailService {
    private String sendEmail = "devjg7797@gmail.com";
    private String sendPw = "앱비밀번호";
    private String smtp_host = "smtp.gmail.com";
    private int smtp_port = 465;  // TLS : 587, SSL : 465

    public boolean SendEmail(String context, String title, String toEmail)
    {
        boolean ret = true;

        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtp_host);
        props.put("mail.smtp.port", smtp_port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", smtp_host);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sendEmail, sendPw);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sendEmail));

            // 받는 이메일
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            // 제목
            message.setSubject(title);

            // 내용
            message.setText(context);

            // 발송
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            ret = false;
        }

        return ret;
    }
}
