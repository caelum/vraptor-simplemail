package br.com.caelum.vraptor.simplemail.template;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import br.com.caelum.vraptor.freemarker.Freemarker;
import br.com.caelum.vraptor.freemarker.Template;

public class DefaultTemplateMail implements TemplateMail {

	private final HashMap<String, DataSource> toEmbed = new HashMap<String, DataSource>();
	private final HashMap<String, DataSource> toAttach = new HashMap<String, DataSource>();

	private final Template template;
	private final String templateName;
	private final Object[] nameParameters;
	private final String appLocation;

	private boolean hasSigner;
	private ResourceBundle bundle;

	public DefaultTemplateMail(String templateName, Freemarker freemarker, String appLocation, ResourceBundle bundle, Object... nameParameters) throws IOException {
		this.templateName = templateName;
		this.appLocation = appLocation;
		this.bundle = bundle;
		this.nameParameters = nameParameters;
		this.template = freemarker.use(templateName);
	}

	@Override
	public TemplateMail with(String key, Object value) {
		if (key.equals("signer")) {
			this.hasSigner = true;
		}
		this.template.with(key, value);
		return this;
	}

	@Override
	public Email to(String name, String toMail) {
		return prepareEmail(name, toMail);
	}

	protected HtmlEmail prepareEmail(String name, String toMail) {
		with("to_name", name);
		with("to_email", toMail);
		with("host", appLocation);
		if (!hasSigner) {
			with("signer", bundle.getString("signer"));
		}

		HtmlEmail email = new HtmlEmail();
		try {

			addEmbeddables(email);
			addAttachments(email);

			email.addTo(toMail, name);

			String subjectMessage = this.bundle.getString(this.templateName);
			String subject = MessageFormat.format(subjectMessage, nameParameters);
			email.setSubject(subject);

			email.setHtmlMsg(this.template.getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return email;
	}

	protected void addEmbeddables(HtmlEmail email) throws EmailException {
		for(Entry<String,DataSource> entry : toEmbed.entrySet()){
			String key = entry.getKey();
			String cid = email.embed(entry.getValue(),key);
			with(key, "cid:" + cid);
		}
	}

	protected void addAttachments(HtmlEmail email) throws EmailException {
		for(Entry<String,DataSource> entry : toAttach.entrySet()){
			email.attach(entry.getValue(),entry.getKey(),"");
		}
	}

	@Override
	public TemplateMail embed(String name, File file) {
		toEmbed.put(name, new FileDataSource(file));
		return this;
	}

	@Override
	public TemplateMail embed(String name, URL url) {
		toEmbed.put(name, new URLDataSource(url));
		return this;
	}

	@Override
	public TemplateMail attach(String name, File file) {
		toAttach.put(name, new FileDataSource(file));
		return this;
	}

	@Override
	public TemplateMail attach(String name, URL url) {
		toAttach.put(name, new URLDataSource(url));
		return this;
	}

}
