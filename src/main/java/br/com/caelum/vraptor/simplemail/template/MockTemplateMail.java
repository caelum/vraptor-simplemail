package br.com.caelum.vraptor.simplemail.template;

public class MockTemplateMail implements TemplateMail {

	private final MockTemplateMailer mailer;

	public MockTemplateMail(MockTemplateMailer mailer) {
		this.mailer = mailer;
	}

	@Override
	public TemplateMail with(String key, Object value) {
		return this;
	}

	@Override
	public void dispatchTo(String name, String toMail) {
		mailer.notifySent(this, name, toMail);
	}

}
