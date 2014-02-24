package br.com.caelum.vraptor.simplemail;

import java.util.Arrays;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.environment.Environment;

/**
 * A simple implementation of a mailer.
 *
 * @author guilherme silveira
 */
public class DefaultMailer implements Mailer {

	private final Environment env;

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMailer.class);
	private static final String EMAIL_LOG_TEMPLATE = "Sending message \"%s\" from %s to %s using server %s:%s (using TLS: %b)";

	private static final String FROM = "vraptor.simplemail.main.from";
	private static final String FROM_NAME = "vraptor.simplemail.main.from.name";
	private static final String SERVER = "vraptor.simplemail.main.server";
	private static final String PORT = "vraptor.simplemail.main.port";
	private static final String TLS = "vraptor.simplemail.main.tls";
	private static final String USERNAME = "vraptor.simplemail.main.username";
	private static final String PASSWORD = "vraptor.simplemail.main.password";
	private static final String REPLY_TO = "vraptor.simplemail.main.replyTo";

	public DefaultMailer(Environment env) {
		this.env = env;
	}

	public void send(Email email) throws EmailException{
		if (email.getFromAddress() == null) {
			email.setFrom(env.get(FROM), env.get(FROM_NAME));
		}
		email.setHostName(env.get(SERVER));
		email.setSmtpPort(Integer.parseInt(env.get(PORT)));
		boolean tls = env.supports(TLS);
		email.setTLS(tls);
		if (tls) {
			email.setAuthenticator(new DefaultAuthenticator(env.get(USERNAME),
					env.get(PASSWORD)));
		}
		if(env.has(REPLY_TO)) {
			String replyTo = env.get(REPLY_TO);
			email.addReplyTo(replyTo);
		}
	    if(env.has(Mailer.DEFAULT_TO_PROPERTIES)){
	    	email.getToAddresses().clear();
	    	email.addTo(env.get(Mailer.DEFAULT_TO_PROPERTIES));
	    	email.getBccAddresses().clear();
	    	email.getCcAddresses().clear();
	    };

		wrapUpAndSend(email);
	}

	protected void wrapUpAndSend(Email email) throws EmailException {
		LOGGER.debug(String.format(emailLogTemplate(),
				email.getSubject(),
				email.getFromAddress(),
				email.getToAddresses(),
				email.getHostName(),
				email.getSmtpPort(),
				email.isTLS()));
		email.send();
	}

	protected String emailLogTemplate() {
		return EMAIL_LOG_TEMPLATE;
	}

}
