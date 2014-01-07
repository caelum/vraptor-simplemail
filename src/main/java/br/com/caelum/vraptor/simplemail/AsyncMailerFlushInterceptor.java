package br.com.caelum.vraptor.simplemail;

import static org.slf4j.LoggerFactory.getLogger;

import javax.inject.Inject;

import org.slf4j.Logger;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

@Intercepts
public class AsyncMailerFlushInterceptor{

	private static final Logger LOGGER = getLogger(AsyncMailerFlushInterceptor.class);

	private AsyncMailer mailer;

	/**
	 * @deprecated CDI eyes only
	 */
	public AsyncMailerFlushInterceptor() {}
	
	@Inject
	public AsyncMailerFlushInterceptor(AsyncMailer mailer) {
		this.mailer = mailer;
	}

	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {
		try {
			stack.next();
			mailer.deliverPostponedMails();
		} finally {
			if (mailer.hasMailToDeliver()) {
				LOGGER.error("The following emails were not delivered because of an exception: {}", mailer.clearPostponedMails());
			}
		}
	}
}