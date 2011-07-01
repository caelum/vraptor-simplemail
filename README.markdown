## vraptor-simplemail

Send e-mails without worrying about the SMTP server settings.

# installing

Vraptor-simplemail.jar can be downloaded from mavens repository, or configured in any compatible tool:

		<dependency>
			<groupId>br.com.caelum.vraptor</groupId>
			<artifactId>vraptor-simplemail</artifactId>
			<version>1.0.1</version>
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

Vraptor-simplemail uses vraptor-environment to manage different mail server configurations
(for development, production, etc.). So, in your different environment configuration files
(development.properties, production.properties, etc.), you put the SMTP server configurations:

	vraptor.simplemail.main.server = localhost
	vraptor.simplemail.main.port = 25
	vraptor.simplemail.main.tls = false
	vraptor.simplemail.main.from = no-reply@myapp.com

# help

Get help from vraptor developers and the community at vraptor mailing list.
