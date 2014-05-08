package br.com.caelum.vraptor.simplemail.template;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
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
	private String templateName;
	private DefaultTemplateMail templateMail;

	@Before
	public void setUp() throws IOException, TemplateException{
		when(template.getContent()).thenReturn("Some message");
		templateName = "templateNameKey";
		templateMail = new DefaultTemplateMail(templateName, template, localization, null, null);
	}

	@Test
	public void should_throw_exception_if_there_is_no_subject_at_messages_properties(){
		when(localization.getMessage(templateName, null)).thenReturn("???"+templateName+"???");
		
		try {
			templateMail.prepareEmail("leo", "leo@leo.com");
			fail();		
		} catch (RuntimeException e) {
			assertTrue(e.getCause().getClass().isAssignableFrom(IllegalArgumentException.class));
		}
	}
	
	@Test
	public void should_not_throw_exception_if_there_is_a_subject_at_messages_properties() throws IOException, TemplateException {
		when(localization.getMessage(templateName, null)).thenReturn("real template title");
		
		DefaultTemplateMail templateMail = new DefaultTemplateMail(templateName, template, localization, null, null);
		
		templateMail.prepareEmail("leo", "leo@leo.com");
	}

}
