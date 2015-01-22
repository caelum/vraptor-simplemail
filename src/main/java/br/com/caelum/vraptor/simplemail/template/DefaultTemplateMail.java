package br.com.caelum.vraptor.simplemail.template;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import br.com.caelum.vraptor.freemarker.Freemarker;
import br.com.caelum.vraptor.freemarker.Template;
import freemarker.template.Configuration;

public class DefaultTemplateMail implements TemplateMail {

	private final HashMap<String, DataSource> toEmbed = new HashMap<String, DataSource>();
	private final HashMap<String, DataSource> toAttach = new HashMap<String, DataSource>();

	private final HashMap<String, String> bccs = new HashMap<String, String>();
	
	private final Template template;
	private final String templateName;
	private final Object[] nameParameters;
	private final String appLocation;

	private boolean hasSigner;
	private BundleFormatter bundle;

	
	public DefaultTemplateMail(String templateName, Freemarker freemarker, String appLocation, Configuration configuration, BundleFormatter bundle, Object... nameParameters) throws IOException {
		this(templateName, freemarker.use(templateName, configuration), appLocation, bundle, nameParameters);
	}

	public DefaultTemplateMail(String templateName, Freemarker freemarker, String appLocation, BundleFormatter bundle, Object... nameParameters) throws IOException {
		this(templateName, freemarker.use(templateName), appLocation, bundle, nameParameters);
	}

	public DefaultTemplateMail(String templateName, Template template, String appLocation, BundleFormatter bundle, Object... nameParameters) throws IOException {
		this.templateName = templateName;
		this.template = template;
		this.appLocation = appLocation;
		this.bundle = bundle;
		this.nameParameters = nameParameters;
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
			with("signer", bundle.getMessage("signer"));
		}

		HtmlEmail email = new HtmlEmail();

		
		email.setCharset("utf-8");
		try {
			Set<Entry<String, String>> bccEntries = bccs.entrySet();
			for (Entry<String, String> entry : bccEntries) {
				email.addBcc(entry.getKey(), entry.getValue());
			}
			addEmbeddables(email);
			addAttachments(email);
			email.addTo(toMail, name);
			boolean hasNoSubjectDefined = this.bundle.getMessage(templateName,
					nameParameters).equals("???" + templateName + "???");
			if (hasNoSubjectDefined) {
				throw new IllegalArgumentException(
						"Subject not defined for email template : " + templateName);
			} else {
				email.setSubject(this.bundle.getMessage(this.templateName, nameParameters));
			}
			email.setHtmlMsg(this.template.getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return email;
	}

	protected void addEmbeddables(HtmlEmail email) throws EmailException {
		for (Entry<String, DataSource> entry : toEmbed.entrySet()) {
			String key = entry.getKey();
			String cid = email.embed(entry.getValue(), key);
			with(key, "cid:" + cid);
		}
	}

	protected void addAttachments(HtmlEmail email) throws EmailException {
		for (Entry<String, DataSource> entry : toAttach.entrySet()) {
			email.attach(entry.getValue(), entry.getKey(), "");
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

	
	@Override
	public TemplateMail addBcc(String name, String email) {
		bccs.put(email, name);
		return this;
	}



}
