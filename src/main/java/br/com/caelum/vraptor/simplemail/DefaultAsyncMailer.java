package br.com.caelum.vraptor.simplemail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple implementation of an asynchronous mailer. It relies upon an instance
 * of {@link ExecutorService} to distribute tasks among threads. 
 *
 * @author luiz
 * @author victorkendy
 */
@RequestScoped
public class DefaultAsyncMailer implements AsyncMailer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAsyncMailer.class);

	private ExecutorService executor;
	private Mailer mailer;
	private Queue<Email> mailQueue = new LinkedList<Email>();

	@Deprecated //CDI eyes only
	public DefaultAsyncMailer() {}
	
	@Inject
	public DefaultAsyncMailer(ExecutorService executor, Mailer mailer) {
		this.executor = executor;
		this.mailer = mailer;
	}

	@Override
	public Future<Void> asyncSend(final Email email) {
		LOGGER.debug("New email to be sent asynchronously: {} to {}", email.getSubject(), email.getToAddresses());
		Callable<Void> task = new Callable<Void>() {
			@Override
			public Void call() throws EmailException {
				LOGGER.debug("Asynchronously sending email {} to {}", email.getSubject(), email.getToAddresses());
				DefaultAsyncMailer.this.mailer.send(email);
				return null;
			}
		};
		return this.executor.submit(task);
	}

	@Override
	public void sendLater(Email email) {
		this.mailQueue.add(email);
	}

	@Override
	public List<Email> clearPostponedMails() {
		List<Email> undeliveredMails = new ArrayList<Email>(this.mailQueue);
		this.mailQueue.clear();
		return undeliveredMails;
	}

	@Override
	public Map<Email, Future<Void>> deliverPostponedMails() {
		Map<Email, Future<Void>> deliveries = new HashMap<Email, Future<Void>>();
		LOGGER.debug("Delivering all {} postponed emails", this.mailQueue.size());
		while (!this.mailQueue.isEmpty()) {
			Email nextMail = this.mailQueue.poll();
			Future<Void> sendingResult = this.asyncSend(nextMail);
			deliveries.put(nextMail, sendingResult);
		}
		return deliveries;
	}

	@Override
	public boolean hasMailToDeliver() {
		return !this.mailQueue.isEmpty();
	}
}
