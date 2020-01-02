/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricvoting;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Prantik
 */
public class EmailSender {

    EmailSender(String to, String vcode) throws AddressException, MessagingException {
        String d_email = "mistertesterfister@gmail.com",
                d_uname = "mistertesterfister@gmail.com",
                d_password = "5thjanuary",
                d_host = "smtp.gmail.com",
                d_port = "465", //465,587
                m_to = to,
                m_subject = "Electronic Voting System - Verify Account",
                m_text = "Please enter the following code in the app to verify your account\n" + vcode;

        Properties props = new Properties();
        props.put("mail.smtp.user", d_email);
        props.put("mail.smtp.host", d_host);
        props.put("mail.smtp.port", d_port);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", d_port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(d_email, d_password);
            }
        };

        Session session = Session.getInstance(props, auth);
        session.setDebug(false);

        MimeMessage msg = new MimeMessage(session);
        msg.setText(m_text);
        msg.setSubject(m_subject);
        msg.setFrom(new InternetAddress(d_email));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(m_to));

        Transport transport = session.getTransport("smtps");
        transport.connect(d_host, 465, d_uname, d_password);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }
}
