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

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

/**
 * A simple implementation of an asynchronous mailer. It relies upon an instance
 * of {@link ExecutorService} to distribute tasks among threads. This
 * {@link ExecutorService} must be provided by a {@link ComponentFactory}.
 *
 * @author luiz
 * @author victorkendy
 */
@Component
@ApplicationScoped
public class DefaultAsyncMailer implements AsyncMailer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAsyncMailer.class);

	private final ExecutorService executor;
	private final Mailer mailer;
	private final Queue<Email> mailQueue = new LinkedList<Email>();

	public DefaultAsyncMailer(ExecutorService executor, Mailer mailer) {
		this.executor = executor;
		this.mailer = mailer;
	}

	@Override
	public Future<Void> asyncSend(final Email email) {
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
		this.mailQueue.offer(email);
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
		while (!this.mailQueue.isEmpty()) {
			Email nextMail = this.mailQueue.poll();
			Future<Void> sendingResult = this.asyncSend(nextMail);
			deliveries.put(nextMail, sendingResult);
		}
		return deliveries;
	}
}
