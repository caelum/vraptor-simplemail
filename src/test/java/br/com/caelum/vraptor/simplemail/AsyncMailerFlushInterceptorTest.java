package br.com.caelum.vraptor.simplemail;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

@RunWith(MockitoJUnitRunner.class)
public class AsyncMailerFlushInterceptorTest {
	@Mock
	private AsyncMailer mailer;
	@Mock
	private InterceptorStack stack;

	private Interceptor interceptor;
	private MyController controller;
	private ResourceMethod method;

	@Before
	public void setUp() throws Exception {
		interceptor = new AsyncMailerFlushInterceptor(mailer);
		controller = new MyController();
		method = createResourceMethod();
	}

	@Test
	public void should_deliver_postponed_emails_after_logic() throws Exception {
		when(mailer.hasMailToDeliver()).thenReturn(false); // happens after stack.next

		interceptor.intercept(stack, method, controller);

		InOrder verifications = inOrder(stack, mailer);
		verifications.verify(stack).next(method, controller);
		verifications.verify(mailer).deliverPostponedMails();
	}

	@Test
	public void should_not_send_and_clear_list_of_postponed_emails_if_an_exception_occurs() throws Exception {
		doThrow(new InterceptionException("test")).when(stack).next(method, controller);
		when(mailer.hasMailToDeliver()).thenReturn(true);

		try {
			interceptor.intercept(stack, method, controller);
			fail("Should have not swallowed exception");
		} catch (InterceptionException e) {
			// ok
		}

		verify(mailer, never()).deliverPostponedMails();
		verify(mailer).clearPostponedMails();
	}

	private ResourceMethod createResourceMethod() {
		Method method = new Mirror().on(MyController.class).reflect().method("anAction").withoutArgs();
		return new DefaultResourceMethod(new DefaultResourceClass(MyController.class), method);
	}

	static class MyController {
		public void anAction() {}
	}
}
