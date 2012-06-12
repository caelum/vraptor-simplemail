package br.com.caelum.vraptor.simplemail.template;

import java.util.HashMap;
import java.util.Map;

public class MockTemplateMailer implements TemplateMailer {

	private final Map<String, MockTemplateMail> sentMails = new HashMap<String, MockTemplateMail>();

	@Override
	public TemplateMail template(String name, Object... nameArgs) {
		return new MockTemplateMail(this);
	}

	public boolean sentEmailTo(String name, String toMail) {
		return sentMails.get(name + ":" + toMail) != null;
	}

	public void notifySent(MockTemplateMail mail, String name, String toMail) {
		sentMails.put(name + ":" + toMail, mail);
	}

}
