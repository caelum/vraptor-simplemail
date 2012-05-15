## vraptor-simplemail

Send e-mails without worrying about the SMTP server settings or blocking the
processing of a request while the e-mail is sent.

# installing

Vraptor-simplemail.jar can be downloaded from mavens repository, or configured in any compatible tool:

		<dependency>
			<groupId>br.com.caelum.vraptor</groupId>
			<artifactId>vraptor-simplemail</artifactId>
			<version>1.1.0</version>
			<scope>compile</scope>
		</dependency>

Vraptor-simplemail depends upon Apache's Commons Email library (http://commons.apache.org/email/).

# usage

In your controller:

	@Resource
	public class PasswordResetterController {

		private final User user;
		private final Mailer mailer;

		public PasswordResetterController(User user, Mailer mailer) {
			this.user = user;
			this.mailer = mailer;
		}

		@Path("/password/send")
		@Post
		public void sendNewPassword() {
			Email email = new SimpleMail();
			email.setSubject("Your new password");
			email.addTo(user.getEmail());
			email.setMsg(user.generateNewPassword());
			mailer.send(email); // Hostname, port and security settings are made by the Mailer
		}

	}

Or, if you want to send e-mails in a different thread, so that the process is
not blocked while the e-mail is sent:

	@Resource
	public class PasswordResetterController {

		private final User user;
		private final AsyncMailer mailer;

		public PasswordResetterController(User user, AsyncMailer mailer) {
			this.user = user;
			this.mailer = mailer;
		}

		@Path("/password/send")
		@Post
		public void sendNewPassword() {
			Email email = new SimpleMail();
			email.setSubject("Your new password");
			email.addTo(user.getEmail());
			email.setMsg(user.generateNewPassword());
			mailer.asyncSend(email); // Hostname, port and security settings are made by the Mailer
		}

	}

In this case, you also need to implement a class to create a thread to send the
e-mail. This is done by implementing a factory of ExecutorService (an interface
from java.util.concurrent package). For instance, if you want to use a thread
pool of a fixed size, you need to create this factory:

	@Component
	@ApplicationScoped
	public class ThreadProvider implements ComponentFactory<ExecutorService> {
		private ExecutorService pool;

		@PostConstruct
		public void initialize() {
			this.pool = Executors.newFixedThreadPool(10);
		}

		@Override
		public ExecutorService getInstance() {
			return this.pool;
		}

		@PreDestroy
		public void shutdown() {
			this.pool.shutdown();
		}
	}

Vraptor-simplemail uses vraptor-environment to manage different mail server configurations
(for development, production, etc.). So, in your different environment configuration files
(development.properties, production.properties, etc.), you put the SMTP server configurations:

	vraptor.simplemail.main.server = localhost
	vraptor.simplemail.main.port = 25
	vraptor.simplemail.main.tls = false
	vraptor.simplemail.main.from = no-reply@myapp.com

When appropriate, configure on your environment properties file the SMTP server replyTo
property as follows:
	vraptor.simplemail.main.replyTo = support@myapp.com

# help

Get help from vraptor developers and the community at vraptor mailing list.
