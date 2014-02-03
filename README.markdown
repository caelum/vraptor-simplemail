## vraptor-simplemail
![Build status](https://secure.travis-ci.org/caelum/vraptor-simplemail.png)

Send e-mails without worrying about the SMTP server settings or blocking the
processing of a request while the e-mail is sent.

# installing

Vraptor-simplemail.jar can be downloaded from mavens repository, or configured in any compatible tool:
```xml
	<dependency>
		<groupId>br.com.caelum.vraptor</groupId>
		<artifactId>vraptor-simplemail</artifactId>
		<version>1.2.1</version>
		<scope>compile</scope>
	</dependency>
```
Vraptor-simplemail depends upon Apache's Commons Email library (http://commons.apache.org/email/).

# usage

In your controller:
```java
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
		Email email = new SimpleEmail();
		email.setSubject("Your new password");
		email.addTo(user.getEmail());
		email.setMsg(user.generateNewPassword());
		mailer.send(email); // Hostname, port and security settings are made by the Mailer
	}

}
```
Or, if you want to send e-mails in a different thread, so that the process is
not blocked while the e-mail is sent:
```java
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
		Email email = new SimpleEmail();
		email.setSubject("Your new password");
		email.addTo(user.getEmail());
		email.setMsg(user.generateNewPassword());
		mailer.asyncSend(email); // Hostname, port and security settings are made by the Mailer
	}

}
```
In this case, you also need to implement a class to create a thread to send the
e-mail. This is done by implementing a factory of ExecutorService (an interface
from java.util.concurrent package). For instance, if you want to use a thread
pool of a fixed size, you need to create this factory:

```java
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
``` 

You can also send e-mails in a transaction-like fashion: send as many e-mails
as you want inside your controller and, if an exception occurs, cancel all
those e-mails. To do so, use the method `sendLater` from the `TemplateMailer`.
The interceptor `AsyncMailerFlushInterceptor` is responsible for sending or
cancelling the e-mails if needed.

# templating

VRaptor-simplemail integrates pretty well with Freemarker. You can use
Freemarker templates for your e-mails, so that you don't need to write the
whole e-mail inside a Java class.

To use simplemail with Freemarker, you will also need vraptor-freemarker. Put
your templates inside a folder called `templates` and, to use them, ask for a
`TemplateMailer` in your controller's constructor.

```java
	@Resource
	public class PasswordResetterController {

		private final User user;
		private final AsyncMailer mailer;
		private final TemplateMailer templates;

		public PasswordResetterController(User user, AsyncMailer mailer, TemplateMailer templates) {
			this.user = user;
			this.mailer = mailer;
			this.templates = templates;
		}

		// controller's methods
	}
```

Then, to create and send an e-mail, specify which template you want to use,
bind the necessary variables and, finally, specify the addressee. In this last
step, you will receive an instance of `Email` ready to be sent.

```java
@Path("/password/send")
@Post
public void sendNewPassword() {
	Email email = this.templates
			.template("forgotMail.ftl")
			.with("user", this.user)
			.with("password", this.user.generateNewPassword())
			.to(this.user.getName(), this.user.getEmail());
	mailer.asyncSend(email); // Hostname, port and security settings are made by the Mailer
}
```

You can also use a custom freemarker configuration calling the method `with(configuration)` before `.template`:

```java
@Path("/password/send")
@Post
public void sendNewPassword() {
	Configuration configuration = new Configuration();
	//configure 

	Email email = this.templates
			.with(configuration)
			.template("forgotMail.ftl")
			.with("user", this.user)
			.with("password", this.user.generateNewPassword())
			.to(this.user.getName(), this.user.getEmail());
	mailer.asyncSend(email); // Hostname, port and security settings are made by the Mailer
}
```

Obs: This configuration will be read on every request. Its NOT application scoped


# environments

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

Under development environment, vraptor-simplemail will use MockMailer to fake the sending of emails. This 
implementation will simply log the emails with sl4j.

Under any other environment, vraptor-simplemail will use DefaultMailer or the class specified 
through `mailer.implementation` property at your vraptor-environment properties file.

# amazon SES
You can also send mails using Amazon SES (http://aws.amazon.com/ses/). To use SES, you need
to configure vraptor-simplemail to use SES mailer in your the properties of your environment:

    mailer.implementation = br.com.caelum.vraptor.simplemail.aws.AmazonSESMailer

This mailer will send real emails only in the "production" environment. In "development" environment
for example, it will only log emails with log4j.

If you still need to send real emails in a environment different than "production", you can configure this behaviour in 
the environment properties file. So, for example, if you need to send real emails with SES in "testing" environment, then
the following configurations must be added to testing.properties:

    mailer.implementation = br.com.caelum.vraptor.simplemail.aws.AmazonSESMailer
    vraptor.simplemail.send_real_email = true

# help

Get help from vraptor developers and the community at vraptor mailing list.
