package br.com.caelum.vraptor.simplemail.template;

import java.io.File;
import java.net.URL;

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
	 * Fills a key with a value for embed a file in the mail body
	 * 
	 * @param name The name of a template variable to be replaced
	 * @param file The file to be embed 
	 * @return The same TemplateMail object
	 */
	TemplateMail embed(String name, File file);
	
	/**
	 * Fills a key with a value for embed a content in the mail body
	 * 
	 * @param name The name of a template variable to be replaced
	 * @param url The url to the content to be embed 
	 * @return The same TemplateMail object
	 */
	TemplateMail embed(String name, URL url);
	
	/**
	 * Attach a file to the email
	 * 
	 * @param name The name of the file
	 * @param file The file to be attached 
	 * @return The same TemplateMail object
	 */
	TemplateMail attach(String name, File file);
	
	/**
	 * Attach a content to the email
	 * 
	 * @param name The name of the file
	 * @param url The URL to the content to be attached 
	 * @return The same TemplateMail object
	 */
	TemplateMail attach(String name, URL url);

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