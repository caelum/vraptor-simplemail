package br.com.caelum.vraptor.simplemail.template;

import java.io.IOException;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.freemarker.Freemarker;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.simplemail.AsyncMailer;

@Component
public class DefaultTemplateMailer implements TemplateMailer {

	private final Freemarker freemarker;
	private final AsyncMailer mailer;
	private final Localization localization;
	private final String appPath;

	public DefaultTemplateMailer(Localization localization, Freemarker freemarker, AsyncMailer mailer, ServletContext context, Environment env) {
		this.localization = localization;
		this.freemarker = freemarker;
		this.mailer = mailer;
		this.appPath = env.get("host") + context.getContextPath();
	}

	@Override
	public TemplateMail template(String name, Object... nameParameters) {
		try {
			return new DefaultTemplateMail(name, freemarker, localization, mailer,appPath, nameParameters);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
