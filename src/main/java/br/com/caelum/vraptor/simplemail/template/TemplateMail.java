package br.com.caelum.vraptor.simplemail.template;

import org.apache.commons.mail.Email;

import br.com.caelum.vraptor.simplemail.AsyncMailer;
import br.com.caelum.vraptor.simplemail.Mailer;

public interface TemplateMail {

	/**
	 * Fills a key with a value for later replacement when assembling the final
	 * e-mail
	 *
	 * @param key
	 *            The name of a template variable to be replaced
	 * @param value
	 *            The value to be used when replacing it
	 * @return The same TemplateMail object
	 */
	TemplateMail with(String key, Object value);

	/**
	 * Assembles the e-mail to the given recipient
	 *
	 * @param name
	 *            Recipient name
	 * @param toMail
	 *            Recipient e-mail
	 * @return An e-mail ready to be sent using a {@link Mailer} or an
	 *         {@link AsyncMailer}
	 */
	public Email to(String name, String toMail);
}