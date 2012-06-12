package br.com.caelum.vraptor.simplemail.template;


public interface TemplateMail {

	TemplateMail with(String key, Object value);

	public void dispatchTo(String name, String toMail);

}