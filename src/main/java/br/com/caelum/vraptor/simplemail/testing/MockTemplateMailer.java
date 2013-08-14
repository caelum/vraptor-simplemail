package br.com.caelum.vraptor.simplemail.testing;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.simplemail.template.TemplateMail;
import br.com.caelum.vraptor.simplemail.template.TemplateMailer;

@Vetoed
public class MockTemplateMailer implements TemplateMailer {

	@Override
	public TemplateMail template(String name, Object... nameParameters) {
		return new MockTemplateMail();
	}

}
