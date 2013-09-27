package br.com.caelum.vraptor.simplemail;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

/**
 * A simple apache based mailer. The mailer will use the server configuration
 * named 'main' from your environment file:
 *
 * vraptor.simplemail.main.server = smtp.gmail.com
 * vraptor.simplemail.main.port = 587
 * vraptor.simplemail.main.tls = true
 * vraptor.simplemail.main.username = your_username
 * vraptor.simplemail.main.password = your_password
 *
 * You can override the authenticator and keep your password somewhere else if you want it.
 *
 * @author guilherme silveira
 */
public interface Mailer {
	
	public static String DEFAULT_TO_PROPERTIES = "vraptor.simplemail.default_to";

	/**
	 * Sends an email using the main mailer configuration. If server, port, tls or authenticator
	 * have been set, they will not be overriden.
	 *
	 * @param email
	 * @throws EmailException
	 */
	void send(Email email) throws EmailException;

}
