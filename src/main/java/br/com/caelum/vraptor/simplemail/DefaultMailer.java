package br.com.caelum.vraptor.simplemail;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * A simple implementaion of a mailer.
 * 
 * @author guilherme silveira
 */
@Component
@ApplicationScoped
public class DefaultMailer implements Mailer {

	private final Environment env;

	private static final String FROM = "vraptor.simplemail.main.from";
	private static final String SERVER = "vraptor.simplemail.main.server";
	private static final String PORT = "vraptor.simplemail.main.port";
	private static final String TLS = "vraptor.simplemail.main.tls";
	private static final String USERNAME = "vraptor.simplemail.main.username";
	private static final String PASSWORD = "vraptor.simplemail.main.password";

	public DefaultMailer(Environment env) {
		this.env = env;
	}

	public void send(Email email) throws EmailException{
		if (email.getFromAddress() == null) {
			email.setFrom(env.get(FROM));
		}
		email.setHostName(env.get(SERVER));
		email.setPort(Integer.parseInt(env.get(PORT)));
		boolean tls = Boolean.parseBoolean(env.supports(TLS));
		email.setTLS(tls);
		if (tls) {
			email.setAuthenticator(new DefaultAuthenticator(env.get(USERNAME),
					env.get(PASSWORD)));
		}
		wrapUpAndSend(email);
	}
	
	protected void wrapUpAndSend(Email email) {
		email.send();
	}

}
