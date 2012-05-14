package br.com.caelum.vraptor.simplemail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.mail.Email;

/**
 * A mailer that sends e-mails asynchronously using the environment's configurations.
 *
 * @author luiz
 * @author victorkendy
 * @see Mailer
 */
public interface AsyncMailer {

	/**
	 * Sends an email asynchronously using the main mailer configuration and the
	 * given {@link ExecutorService}. If server, port, tls or authenticator have
	 * been set, they will not be overriden.
	 *
	 * @param email
	 *            The e-mail to be sent
	 * @return a future so that you can check if the e-mail was sent or cancel
	 *         it
	 */
	Future<Void> asyncSend(final Email email);
}
