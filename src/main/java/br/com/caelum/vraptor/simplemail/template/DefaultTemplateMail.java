package br.com.caelum.vraptor.simplemail.template;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import freemarker.template.Configuration;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.freemarker.Freemarker;
import br.com.caelum.vraptor.freemarker.Template;

public class DefaultTemplateMail implements TemplateMail {
	
	private final HashMap<String, DataSource> toEmbed = new HashMap<String, DataSource>();
	private final HashMap<String, DataSource> toAttach = new HashMap<String, DataSource>();

	private final Template template;
	private final Localization localization;
	private final String templateName;
	private final Object[] nameParameters;
	private final String appLocation;

	private boolean hasSigner;

	public DefaultTemplateMail(String templateName, Freemarker freemarker, Localization localization, String appLocation, Configuration configuration, Object... nameParameters) throws IOException {
		this(templateName, freemarker.use(templateName, configuration), localization, appLocation, nameParameters);
	}

	public DefaultTemplateMail(String templateName, Freemarker freemarker, Localization localization, String appLocation, Object... nameParameters) throws IOException {
		this(templateName, freemarker.use(templateName), localization, appLocation, nameParameters);
	}

	public DefaultTemplateMail(String templateName, Template template, Localization localization, String appLocation, Object... nameParameters) throws IOException {
		this.templateName = templateName;
		this.template = template;
		this.localization = localization;
		this.appLocation = appLocation;
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
			with("signer", this.localization.getMessage("signer"));
		}

		HtmlEmail email = new HtmlEmail();
		email.setCharset("utf-8");
		
		try {
			
			addEmbeddables(email);
			addAttachments(email);
			
			email.addTo(toMail, name);
			boolean hasNoSubjectDefined = this.localization.getMessage(templateName,
					nameParameters).equals("???" + templateName + "???");
			if (hasNoSubjectDefined) {
				throw new IllegalArgumentException(
						"Subject not defined for email template : " + templateName);
			} else {
				email.setSubject(this.localization.getMessage(this.templateName, nameParameters));
			}
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
