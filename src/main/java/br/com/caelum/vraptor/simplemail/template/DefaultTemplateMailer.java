package br.com.caelum.vraptor.simplemail.template;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.freemarker.Freemarker;
import br.com.caelum.vraptor4.core.Localization;

public class DefaultTemplateMailer implements TemplateMailer {

	private Freemarker freemarker;
	private Localization localization;
	private String appPath;
	
	@Deprecated //CDI eyes only
	public DefaultTemplateMailer() {}
	
	@Inject
	public DefaultTemplateMailer(Localization localization, Freemarker freemarker, ServletContext context, Environment env) {
		this.localization = localization;
		this.freemarker = freemarker;
		this.appPath = env.get("host") + context.getContextPath();
	}

	@Override
	public TemplateMail template(String name, Object... nameParameters) {
		try {
			return new DefaultTemplateMail(name, freemarker, localization, appPath, nameParameters);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
