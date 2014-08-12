package br.com.caelum.vraptor.simplemail.template;

import java.io.IOException;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.freemarker.Freemarker;
import br.com.caelum.vraptor.ioc.Component;
import freemarker.template.Configuration;

@Component
public class DefaultTemplateMailer implements TemplateMailer {

	private final Freemarker freemarker;
	private final Localization localization;
	private Configuration configuration;
	private ServletContext context;
	private Environment env;

	public DefaultTemplateMailer(Localization localization, Freemarker freemarker, ServletContext context, Environment env) {
		this.localization = localization;
		this.freemarker = freemarker;
		this.context = context;
		this.env = env;
	}

	@Override
	public TemplateMail template(String name, Object... nameParameters) {
		String appPath = env.get("host") + context.getContextPath();
		try {
			boolean shouldUseCustomConfiguration = configuration != null;
			if(shouldUseCustomConfiguration)
				return new DefaultTemplateMail(name, freemarker, localization, appPath, configuration, nameParameters);
			
			return new DefaultTemplateMail(name, freemarker, localization, appPath, nameParameters);
			
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public TemplateMailer with(Configuration configuration) {
		DefaultTemplateMailer defaultTemplateMailer = new DefaultTemplateMailer(localization, freemarker, context, env);
		defaultTemplateMailer.setConfiguration(configuration);
		return defaultTemplateMailer;
	}

	private void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

}
