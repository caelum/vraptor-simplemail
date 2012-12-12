package br.com.caelum.vraptor.simplemail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

@Intercepts(before = ExecuteMethodInterceptor.class)
@RequestScoped
public class AsyncMailerFlushInterceptor implements Interceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncMailerFlushInterceptor.class);

	private final AsyncMailer mailer;

	public AsyncMailerFlushInterceptor(AsyncMailer mailer) {
		this.mailer = mailer;
	}

	@Override
	public boolean accepts(ResourceMethod method) {
		return true;
	}

	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method, Object controller) throws InterceptionException {
		try {
			stack.next(method, controller);
			mailer.deliverPostponedMails();
		} finally {
			if (mailer.hasMailToDeliver()) {
				LOGGER.error("The following emails were not delivered because of an exception: {}", mailer.clearPostponedMails());
			}
		}
	}
}