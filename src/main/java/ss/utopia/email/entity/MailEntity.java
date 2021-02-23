package ss.utopia.email.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailEntity {
  private String from;
  private String fromName;
  private String to;
  private String subject;
  private String HTMLBody;
}
