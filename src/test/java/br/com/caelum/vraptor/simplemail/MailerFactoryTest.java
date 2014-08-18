package br.com.caelum.vraptor.simplemail;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.simplemail.aws.MockMailer;

@RunWith(MockitoJUnitRunner.class)
public class MailerFactoryTest {
	@Mock
	private Environment env;

	private MailerFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new MailerFactory(env);
	}

	@Test
	public void should_create_a_mock_mailer_if_in_development() throws Exception {
		when(env.getName()).thenReturn("development");
		assertThat(factory.getInstance(), instanceOf(MockMailer.class));
	}

	@Test
	public void should_create_mailer_from_properties_if_specified_and_not_in_development() throws Exception {
		when(env.getName()).thenReturn("production");
		when(env.has(MailerFactory.MAILER_IMPLEMENTATION)).thenReturn(true);
		when(env.get(MailerFactory.MAILER_IMPLEMENTATION)).thenReturn(MyMailer.class.getName());
		assertThat(factory.getInstance(), instanceOf(MyMailer.class));
	}

	@Test
	public void should_create_default_mailer_if_no_mailer_in_properties_and_not_in_development() throws Exception {
		when(env.getName()).thenReturn("production");
		when(env.has(MailerFactory.MAILER_IMPLEMENTATION)).thenReturn(false);
		assertThat(factory.getInstance(), instanceOf(DefaultMailer.class));
	}

	@Test
	public void should_create_mailer_passing_environment_in_constructor_if_needed() throws Exception {
		when(env.getName()).thenReturn("production");
		when(env.has(MailerFactory.MAILER_IMPLEMENTATION)).thenReturn(true);
		when(env.get(MailerFactory.MAILER_IMPLEMENTATION)).thenReturn(MyMailerWithEnv.class.getName());

		Mailer secondMailer = factory.getInstance();
		assertThat(secondMailer, instanceOf(MyMailerWithEnv.class));

		MyMailerWithEnv mailerWithEnv = (MyMailerWithEnv) secondMailer;
		assertThat(mailerWithEnv.getMyEnv(), equalTo(env));
	}
	
	@Test
	public void should_create_mailer_from_properties_if_specified_and_in_development() throws Exception {
		when(env.getName()).thenReturn("development");
		when(env.has(MailerFactory.MAILER_IMPLEMENTATION)).thenReturn(true);
		when(env.get(MailerFactory.MAILER_IMPLEMENTATION)).thenReturn(MyMailer.class.getName());
		assertThat(factory.getInstance(), instanceOf(MyMailer.class));
	}

	public static class MyMailer implements Mailer {
		@Override
		public void send(Email email) throws EmailException {
			// does nothing
		}
	}

	public static class MyMailerWithEnv implements Mailer {
		private final Environment myEnv;

		public MyMailerWithEnv(Environment env) {
			this.myEnv = env;
		}

		@Override
		public void send(Email email) throws EmailException {
			// does nothing
		}

		public Environment getMyEnv() {
			return this.myEnv;
		}
	}
}
