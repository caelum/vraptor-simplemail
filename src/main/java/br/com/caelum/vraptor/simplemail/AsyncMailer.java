package br.com.caelum.vraptor.simplemail;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.mail.Email;

/**
 * A mailer that sends e-mails asynchronously using the environment's
 * configurations.
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
	Future<Void> asyncSend(Email email);

	/**
	 * Schedule an email to be sent later. This email will be delivered only
	 * when the method #deliverPostponedMails is called, if
	 * {@link #clearPostponedMails()} was not called before.
	 *
	 * @param email
	 *            The e-mail to be sent
	 */
	void sendLater(Email email);

	/**
	 * Send all the e-mails that were scheduled by calls to
	 * {@link #sendLater(Email)}.
	 *
	 * @return A map of futures, so that you can check if each e-mail was sent
	 *         or cancel each one
	 */
	Map<Email, Future<Void>> deliverPostponedMails();

	/**
	 * Clears the list of postponed mails, filled by previous calls of
	 * {@link #sendLater(Email)}.
	 *
	 * @return The list of e-mails that were not sent
	 */
	List<Email> clearPostponedMails();

	/**
	 * @return true if there are e-mails to be delivered later by a call to
	 *         {@link #deliverPostponedMails()}
	 */
	boolean hasMailToDeliver();
}
