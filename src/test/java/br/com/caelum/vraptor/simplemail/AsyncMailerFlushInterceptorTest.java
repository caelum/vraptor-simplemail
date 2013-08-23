package br.com.caelum.vraptor.simplemail;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.interceptor.SimpleInterceptorStack;

@RunWith(MockitoJUnitRunner.class)
public class AsyncMailerFlushInterceptorTest {
	@Mock
	private AsyncMailer mailer;
	@Mock
	private SimpleInterceptorStack stack;

	private AsyncMailerFlushInterceptor interceptor;

	@Before
	public void setUp() throws Exception {
		interceptor = new AsyncMailerFlushInterceptor(mailer);
	}

	@Test
	public void should_deliver_postponed_emails_after_logic() throws Exception {
		when(mailer.hasMailToDeliver()).thenReturn(false); // happens after stack.next

		interceptor.intercept(stack);

		InOrder verifications = inOrder(stack, mailer);
		verifications.verify(stack).next();
		verifications.verify(mailer).deliverPostponedMails();
	}

	@Test
	public void should_not_send_and_clear_list_of_postponed_emails_if_an_exception_occurs() throws Exception {
		doThrow(new InterceptionException("test")).when(stack).next();
		when(mailer.hasMailToDeliver()).thenReturn(true);

		try {
			interceptor.intercept(stack);
			fail("Should have not swallowed exception");
		} catch (InterceptionException e) {
			// ok
		}

		verify(mailer, never()).deliverPostponedMails();
		verify(mailer).clearPostponedMails();
	}

}
