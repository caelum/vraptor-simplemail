package br.com.caelum.vraptor.simplemail.template;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.freemarker.Freemarker;

public class DefaultTemplateMailer implements TemplateMailer {

	private Freemarker freemarker;
	private String appPath;
	private ResourceBundle bundle;
	
	@Deprecated //CDI eyes only
	public DefaultTemplateMailer() {}
	
	@Inject
	public DefaultTemplateMailer(Freemarker freemarker, ServletContext context, Environment env, ResourceBundle bundle) {
		this.freemarker = freemarker;
		this.bundle = bundle;
		this.appPath = env.get("host") + context.getContextPath();
	}

	@Override
	public TemplateMail template(String name, Object... nameParameters) {
		try {
			return new DefaultTemplateMail(name, freemarker, appPath, bundle, nameParameters);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
