package br.com.caelum.vraptor.simplemail.template;

import freemarker.template.Configuration;

public interface TemplateMailer {

	TemplateMail template(String name, Object... nameParameters);

	TemplateMailer with(Configuration configuration);

}