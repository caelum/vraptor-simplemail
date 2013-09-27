package br.com.caelum.vraptor.simplemail;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.simplemail.aws.MockMailer;

@Component
@ApplicationScoped
public class MailerFactory implements ComponentFactory<Mailer> {

	public static final String MAILER_IMPLEMENTATION = "mailer.implementation";
	private static final Logger LOGGER = LoggerFactory.getLogger(MailerFactory.class);
	private final Environment env;

	public MailerFactory(Environment env) {
		this.env = env;
	}

	@Override
	public Mailer getInstance() {
		Mailer instance = grabInstance();
		LOGGER.debug("using mailer named " + instance.getClass().getName() + "@" + env.getName());
		return instance;
	}

	private Mailer grabInstance() {
		try {
			return instantiateWithEnv();
		} catch (Exception e) {
			try {
				return instantiate();
			} catch (Exception e2) {
				throw new RuntimeException("Unable to start mailer", e);
			}
		}
	}

	private Mailer instantiateWithEnv() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class<?> implementation = getImplementationName();
		return (Mailer) implementation.getConstructor(Environment.class)
				.newInstance(env);
	}

	private Mailer instantiate() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class<?> implementation = getImplementationName();
		return (Mailer) implementation.newInstance();
	}

	private Class<?> getImplementationName() throws ClassNotFoundException {
		
		boolean hasNoImplementation = !env.has(MAILER_IMPLEMENTATION);
		boolean isDevelopment = env.getName().equals("development");
		if (hasNoImplementation && isDevelopment) {
			return MockMailer.class;
		}

		if (hasNoImplementation) {
			return DefaultMailer.class;
		}
		return Class.forName(env.get(MAILER_IMPLEMENTATION));
	}

}
