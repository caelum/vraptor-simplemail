package br.com.caelum.vraptor.simplemail.template;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.freemarker.Template;
import freemarker.template.TemplateException;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTemplateMailTest {

	@Mock
	private Localization localization;
	@Mock
	private Template template;

	@Test
	public void should_throw_exception_if_there_is_no_subject_at_messages_properties() throws IOException, TemplateException {
		String templateName = "teste";
		when(localization.getMessage(templateName, null)).thenReturn("???"+templateName+"???");
		when(template.getContent()).thenReturn("Some message");
		
		DefaultTemplateMail templateMail = new DefaultTemplateMail(templateName, template, localization, null, null);
		
		try {
			templateMail.prepareEmail("leo", "leo@leo.com");
			fail();		
		} catch (RuntimeException e) {
			assertTrue(e.getCause().getClass().isAssignableFrom(IllegalArgumentException.class));
		}
		
	}

}
