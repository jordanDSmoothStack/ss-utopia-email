package ss.utopia.email.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ss.utopia.email.dto.EmailDto;
import ss.utopia.email.service.MailService;

@Slf4j
@RestController
@RequestMapping(EndpointConstants.UTOPIA_ENDPOINT)
public class MailController {

  private final MailService mailservice;

  public MailController(MailService mailman) {
    mailservice = mailman;
  }

  @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> sendMail(@RequestBody EmailDto emailDto){
    log.info("Sending mail with following content: " + emailDto);
    return mailservice.sendMail(emailDto);
  }
}
