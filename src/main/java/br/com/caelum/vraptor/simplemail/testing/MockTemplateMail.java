package br.com.caelum.vraptor.simplemail.testing;

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

}
