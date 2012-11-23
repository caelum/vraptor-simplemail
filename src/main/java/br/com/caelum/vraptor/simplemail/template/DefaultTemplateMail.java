package br.com.caelum.vraptor.simplemail.template;

import java.io.IOException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.freemarker.Freemarker;
import br.com.caelum.vraptor.freemarker.Template;

public class DefaultTemplateMail implements TemplateMail {

	private final Template template;
	private final Localization localization;
	private final String templateName;
	private final Object[] nameParameters;
	private final String appLocation;

	private boolean hasSigner;

	public DefaultTemplateMail(String templateName, Freemarker freemarker, Localization localization, String appLocation, Object... nameParameters) throws IOException {
		this.templateName = templateName;
		this.appLocation = appLocation;
		this.nameParameters = nameParameters;
		this.template = freemarker.use(templateName);
		this.localization = localization;
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
		try {
			email.addTo(toMail, name);
			email.setSubject(this.localization.getMessage(this.templateName, nameParameters));
			email.setHtmlMsg(this.template.getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return email;
	}

}
