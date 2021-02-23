package ss.utopia.email.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ss.utopia.email.dto.EmailDto;
import ss.utopia.email.entity.MailEntity;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

  static final String SMTPUsername = "AKIA5REGZ56II7SJYCVK";
  static final String SMTPPassword = "BCS3eyH+7d4uQDLpX8HYPbPVj3brBddQfE3dJeANU06O";
  static final String from = "jordan.divina@smoothstack.com";
  static final String fromName = "Utopia Airlines";

  @Override
  public ResponseEntity<String> sendMail(EmailDto emailDto) {
    final String to = emailDto.getEmail();
    final String subject = emailDto.getSubject();
    final String body = emailDto.getContent();

    MailEntity mailToBeSent = new MailEntity();
    mailToBeSent.setFrom(from);
    mailToBeSent.setFromName(fromName);
    mailToBeSent.setTo(to);
    mailToBeSent.setSubject(subject);
    mailToBeSent.setHTMLBody(body);

    try
    {
      return sendMailWithAWS(mailToBeSent);
    }
      catch (UnsupportedEncodingException | MessagingException e)
    {
      log.error(e.getMessage());
      return ResponseEntity.badRequest().body("Email was not sent.");
    }
  }

  private ResponseEntity<String> sendMailWithAWS(MailEntity mail) throws UnsupportedEncodingException, MessagingException {
    final String host = "email-smtp.us-east-1.amazonaws.com";
    final Integer port = 587;

    Properties props = System.getProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.port", port);
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.auth", "true");

    Session session = Session.getDefaultInstance(props);

    MimeMessage msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(mail.getFrom(), mail.getFromName()));
    msg.setRecipient(RecipientType.TO, new InternetAddress(mail.getTo()));
    msg.setSubject(mail.getSubject());
    msg.setContent(mail.getHTMLBody(), "text/html");

    try (Transport transport = session.getTransport()) {
        log.info("Sending a message to: " + mail.getTo());
        transport.connect(host, SMTPUsername, SMTPPassword);
        transport.sendMessage(msg, msg.getAllRecipients());
        log.info("Message was successfully created");
        return ResponseEntity.ok("Email was successfully sent through SES");
    } catch (Exception ex) {
        log.error("Message was not sent");
        log.error("Error message: " + ex.getMessage());
        return ResponseEntity.badRequest().body("Email was not sent.");
    }
  }
}
