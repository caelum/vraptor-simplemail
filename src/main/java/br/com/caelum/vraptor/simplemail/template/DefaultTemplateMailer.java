package br.com.caelum.vraptor.simplemail.template;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.freemarker.Freemarker;
import freemarker.template.Configuration;

public class DefaultTemplateMailer implements TemplateMailer {

	private final Freemarker freemarker;
	private final BundleFormatter bundle;
	private Configuration configuration;
	private final ServletContext context;
	private final Environment env;

	@Deprecated //CDI eyes only
	public DefaultTemplateMailer() {
		this(null, null, null, null);
	}

	@Inject
	public DefaultTemplateMailer(Freemarker freemarker, ServletContext context, Environment env, BundleFormatter bundle) {
		this.freemarker = freemarker;
		this.bundle = bundle;
		this.context = context;
		this.env = env;
	}

	@Override
	public TemplateMail template(String name, Object... nameParameters) {
		String appPath = env.get("host") + context.getContextPath();
		try {
			boolean shouldUseCustomConfiguration = configuration != null;
			if (shouldUseCustomConfiguration)
				return new DefaultTemplateMail(name, freemarker,
						appPath, configuration, bundle, nameParameters);			
			return new DefaultTemplateMail(name, freemarker, appPath, bundle, nameParameters);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public TemplateMailer with(Configuration configuration) {
		DefaultTemplateMailer defaultTemplateMailer = new DefaultTemplateMailer(freemarker, context, env, bundle);
			defaultTemplateMailer.setConfiguration(configuration);
			return defaultTemplateMailer;
		}
		
	private void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}