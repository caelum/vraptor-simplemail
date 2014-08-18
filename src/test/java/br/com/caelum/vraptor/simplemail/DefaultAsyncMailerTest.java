package br.com.caelum.vraptor.simplemail;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class DefaultAsyncMailerTest {
	@Mock
	private ExecutorService mockExecutor;
	@Mock
	private Mailer mockMailer;
	private AsyncMailer mailer;

	@Before
	public void setUp() throws Exception {
		mailer = new DefaultAsyncMailer(mockExecutor, mockMailer);
		setupMockExecutorService();
	}

	@Test
	public void should_submit_task_to_executor_that_delivers_email() throws Exception {
		Email email = new SimpleEmail();

		mailer.asyncSend(email);

		verify(mockMailer, times(1)).send(email);
		verifyNoMoreInteractions(mockMailer);
	}

	@Test
	public void should_schedule_mails_to_be_sent_later_and_send_them() throws Exception {
		Email mail = new SimpleEmail();
		Email anotherMail = new SimpleEmail();

		assertFalse(mailer.hasMailToDeliver());

		mailer.sendLater(mail);
		mailer.sendLater(anotherMail);

		assertTrue(mailer.hasMailToDeliver());

		Map<Email, Future<Void>> delivered = mailer.deliverPostponedMails();

		assertThat(delivered.keySet(), containsInAnyOrder(mail, anotherMail));
		verify(mockMailer, times(1)).send(mail);
		verify(mockMailer, times(1)).send(anotherMail);

		assertFalse(mailer.hasMailToDeliver());

		verifyNoMoreInteractions(mockMailer);
	}

	@Test
	public void should_cancel_mails_to_be_sent_later() throws Exception {
		Email mail = new SimpleEmail();
		Email anotherMail = new SimpleEmail();

		mailer.sendLater(mail);
		mailer.sendLater(anotherMail);

		List<Email> notSent = mailer.clearPostponedMails();

		assertThat(notSent, containsInAnyOrder(mail, anotherMail));
		verify(mockMailer, never()).send(any(Email.class));
	}

	@Test
	public void should_tell_if_has_mail_to_be_delivered() throws Exception {
		Email mail = new SimpleEmail();
		Email anotherMail = new SimpleEmail();

		assertFalse(mailer.hasMailToDeliver());

		mailer.sendLater(mail);
		mailer.sendLater(anotherMail);

		assertTrue(mailer.hasMailToDeliver());

		mailer.deliverPostponedMails();

		assertFalse(mailer.hasMailToDeliver());
	}
	
	private void setupMockExecutorService() throws Exception {
		Answer<?> sendMail = new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Callable task = (Callable) invocation.getArguments()[0];
				task.call();
				return null;
			}
		};
		when(mockExecutor.submit(any(Callable.class))).thenAnswer(sendMail);
	}
}
