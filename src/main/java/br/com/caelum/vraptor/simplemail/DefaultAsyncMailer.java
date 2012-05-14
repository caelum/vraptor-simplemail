package br.com.caelum.vraptor.simplemail;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

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
public class DefaultAsyncMailer implements AsyncMailer {

	private final ExecutorService executor;
	private final Mailer mailer;

	public DefaultAsyncMailer(ExecutorService executor, Mailer mailer) {
		this.executor = executor;
		this.mailer = mailer;
	}

	@Override
	public Future<Void> asyncSend(final Email email) {
		return this.executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws EmailException {
				DefaultAsyncMailer.this.mailer.send(email);
				return null;
			}
		});
	}
}
