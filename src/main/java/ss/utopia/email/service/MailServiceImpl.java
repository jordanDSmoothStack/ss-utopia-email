package ss.utopia.email.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ss.utopia.email.dto.EmailDto;
import ss.utopia.email.entity.MailEntity;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

  static final EnvironmentVariableCredentialsProvider AWSCredentials = new EnvironmentVariableCredentialsProvider();
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

    try {
      return sendMailWithAWS(mailToBeSent);
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().body("Email was not properly formatted");
    }
  }

  private ResponseEntity<String> sendMailWithAWS(MailEntity mail) throws IOException {
    try {
      AmazonSimpleEmailService client =
          AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(
              new AWSStaticCredentialsProvider(AWSCredentials.getCredentials())).withRegion(Regions.US_EAST_1).build();
      SendEmailRequest request = new SendEmailRequest()
          .withDestination(
              new Destination().withToAddresses(mail.getTo()))
          .withMessage(new Message()
              .withBody(new Body()
                  .withHtml(new Content()
                      .withCharset("UTF-8").withData(mail.getHTMLBody())))
              .withSubject(new Content()
                  .withCharset("UTF-8").withData(mail.getSubject())))
          .withSource(mail.getFrom());
      client.sendEmail(request);
      log.info("Email sent to " + mail.getTo());
      return ResponseEntity.ok("Email was sent to " + mail.getTo());
    } catch (Exception ex) {
      log.error("Email was not sent there was an error" + ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There was a problem: " + ex.getMessage());
    }
  }
}
