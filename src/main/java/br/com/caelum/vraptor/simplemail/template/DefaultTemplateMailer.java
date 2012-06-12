package br.com.caelum.vraptor.simplemail.template;

import java.io.IOException;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.freemarker.Freemarker;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.simplemail.AsyncMailer;

@Component
public class DefaultTemplateMailer implements TemplateMailer {

	private final Freemarker freemarker;
	private final AsyncMailer mailer;
	private final Localization localization;

	public DefaultTemplateMailer(Localization localization, Freemarker freemarker, AsyncMailer mailer) {
		this.localization = localization;
		this.freemarker = freemarker;
		this.mailer = mailer;
	}

	@Override
	public TemplateMail template(String name, Object... nameParameters) {
		try {
			return new DefaultTemplateMail(name, freemarker, localization, mailer, nameParameters);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
