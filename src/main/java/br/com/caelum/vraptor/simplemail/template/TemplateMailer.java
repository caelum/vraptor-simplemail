package br.com.caelum.vraptor.simplemail.template;

public interface TemplateMailer {

	TemplateMail template(String name, Object... nameParameters);

}