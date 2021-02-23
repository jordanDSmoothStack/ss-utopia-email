package ss.utopia.email.service;


import org.springframework.http.ResponseEntity;
import ss.utopia.email.dto.EmailDto;

public interface MailService {

  ResponseEntity<String> sendMail(EmailDto emailDto);
}
