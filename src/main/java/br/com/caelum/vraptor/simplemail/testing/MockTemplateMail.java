package br.com.caelum.vraptor.simplemail.testing;

import java.io.File;
import java.net.URL;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import br.com.caelum.vraptor.simplemail.template.TemplateMail;

public class MockTemplateMail implements TemplateMail {

	@Override
	public TemplateMail with(String key, Object value) {
		return this;
	}

	@Override
	public Email to(String name, String toMail) {
		SimpleEmail email = new SimpleEmail();
		try {
			email.addTo(toMail, name);
		} catch (EmailException e) {
			throw new IllegalArgumentException(e);
		}
		return email;
	}

	@Override
	public TemplateMail embed(String name, File file) {
		return this;
	}

	@Override
	public TemplateMail embed(String name, URL url) {
		return this;
	}

	@Override
	public TemplateMail attach(String name, File file) {
		return this;
	}

	@Override
	public TemplateMail attach(String name, URL url) {
		return this;
	}

}
