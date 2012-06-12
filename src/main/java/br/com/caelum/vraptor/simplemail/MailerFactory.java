package br.com.caelum.vraptor.simplemail;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.simplemail.aws.MockMailer;

@Component
@ApplicationScoped
public class MailerFactory implements ComponentFactory<Mailer> {

	private static final String MAILER_IMPLEMENTATION = "mailer.implementation";
	private final Environment env;

	public MailerFactory(Environment env) {
		this.env = env;
	}

	@Override
	public Mailer getInstance() {
		if (env.getName().equals("development")) {
			return new MockMailer();
		}
		try {
			Class<?> implementation = getImplementationName();
			return (Mailer) implementation.getConstructor(Environment.class)
					.newInstance(env);
		} catch (Exception e) {
			throw new RuntimeException("Unable to start mailer", e);
		}
	}

	private Class<?> getImplementationName() throws ClassNotFoundException {
		if (!env.has(MAILER_IMPLEMENTATION))
			return DefaultMailer.class;
		return Class.forName(env.get(MAILER_IMPLEMENTATION));
	}

}
