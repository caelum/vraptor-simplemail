package br.com.caelum.vraptor.simplemail.aws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.simplemail.Mailer;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

@ApplicationScoped
public class AmazonSESMailer implements Mailer {
	private final Environment env;
	private final Session session;
	private final AmazonSimpleEmailServiceClient client;

	public AmazonSESMailer(Environment env) throws IOException {
		this.env = env;
		InputStream resource = AmazonSESMailer.class
				.getResourceAsStream("/AwsCredentials.properties");
		PropertiesCredentials credentials = new PropertiesCredentials(resource);
		this.client = new AmazonSimpleEmailServiceClient(credentials);
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "aws");
		props.setProperty("mail.aws.user", credentials.getAWSAccessKeyId());
		props.setProperty("mail.aws.password", credentials.getAWSSecretKey());
		this.session = Session.getInstance(props);
	}

	private final static Logger logger = LoggerFactory
			.getLogger(AmazonSESMailer.class);
	private final static String FROM = "vraptor.simplemail.main.from";
	private final static String REPLY_TO = "vraptor.simplemail.main.replyTo";

	RawMessage mail2Content(Email email) throws IOException,
			MessagingException, EmailException {
		email.buildMimeMessage();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		email.getMimeMessage().writeTo(out);
		return new RawMessage().withData(ByteBuffer.wrap(out.toByteArray()));
	}

	public void send(Email email) throws EmailException{
		if (env.getName().equals("production")) {
			logger.info("REAL MAIL ::: {} ::: {}", email.getSubject(),
					email.getToAddresses());

			if (email.getFromAddress() == null) {
				email.setFrom(env.get(FROM));
			}
			if (env.has(REPLY_TO)) {
				email.addReplyTo(env.get(REPLY_TO));
			}

			email.setMailSession(session);

			try {
				client.sendRawEmail(new SendRawEmailRequest()
						.withRawMessage(mail2Content(email)));
			} catch (Exception e) {
				throw new EmailException(e);
			}
		} else {
			new MockMailer().send(email);
		}
	}
}