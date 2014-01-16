package br.com.caelum.vraptor.simplemail.template;

import freemarker.template.Configuration;

public interface TemplateMailer {

	TemplateMailer with(Configuration configuration);
	TemplateMail template(String name, Object... nameParameters);

}