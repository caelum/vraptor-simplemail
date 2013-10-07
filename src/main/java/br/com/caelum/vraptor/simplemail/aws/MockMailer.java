package br.com.caelum.vraptor.simplemail.aws;
import net.vidageek.mirror.dsl.Mirror;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;

import br.com.caelum.vraptor.simplemail.Mailer;

public class MockMailer implements Mailer {
	private final static Logger logger = org.slf4j.LoggerFactory.getLogger(MockMailer.class);

	private void send(HtmlEmail email) {
    logger.info("body => {}", new Mirror().on(email).get().field("html"));
    }

	private void send(SimpleEmail email) {
	      logger.info("body => {}", new Mirror().on(email).get().field("content"));
  }

	@Override
	public void send(Email email) throws EmailException {
	    logger.info("subject => {}",email.getSubject());
	    logger.info("from => {}",email.getFromAddress());
	    logger.info("toAddresses => {}",email.getToAddresses());
		if(email instanceof SimpleEmail)
			send((SimpleEmail) email);
		else if(email instanceof HtmlEmail)
			send((HtmlEmail) email);
		else
			logger.info("body => unknown");
	}
}