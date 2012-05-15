## vraptor-simplemail

Envie e-mails sem se preocupar com as configurações do servidor SMTP
ou com bloquear o processamento enquanto envia um e-mail.

# instalação

O vraptor-simplemail.jar pode ser baixado dos repositórios do Maven, ou configurado em qualquer ferramenta
compatível:

		<dependency>
			<groupId>br.com.caelum.vraptor</groupId>
			<artifactId>vraptor-simplemail</artifactId>
			<version>1.1.0</version>
			<scope>compile</scope>
		</dependency>

O vraptor-simplemail depende da biblioteca Commons Email, da Apache (http://commons.apache.org/email/).

# uso

No seu controlador:

	@Resource
	public class PasswordResetterController {

		private final User user;
		private final Mailer mailer;

		public PasswordResetterController(User user, Mailer mailer) {
			this.user = user;
			this.mailer = mailer;
		}

		@Path("/password/send")
		@Post
		public void sendNewPassword() {
			Email email = new SimpleMail();
			email.setSubject("Your new password");
			email.addTo(user.getEmail());
			email.setMsg(user.generateNewPassword());
			mailer.send(email); // As configurações restantes são feitas pelo Mailer
		}

	}

Ou, se você quiser enviar e-mails numa thread diferente, para não
bloquear o processamento enquanto o e-mail é enviado:

	@Resource
	public class PasswordResetterController {

		private final User user;
		private final AsyncMailer mailer;

		public PasswordResetterController(User user, AsyncMailer mailer) {
			this.user = user;
			this.mailer = mailer;
		}

		@Path("/password/send")
		@Post
		public void sendNewPassword() {
			Email email = new SimpleMail();
			email.setSubject("Your new password");
			email.addTo(user.getEmail());
			email.setMsg(user.generateNewPassword());
			mailer.asyncSend(email); // Hostname, port and security settings are made by the Mailer
		}

	}

Neste caso, você também precisa implementar uma classe para criar uma thread
para enviar o e-mail. Para tanto, é necessário implementar uma factory de
ExecutorService (uma interface do pacote java.util.concurrent). Por exemplo, se
você quiser usar um pool de threads de tamanho fixo, você precisa criar essa
factory:

	@Component
	@ApplicationScoped
	public class ThreadProvider implements ComponentFactory<ExecutorService> {
		private ExecutorService pool;

		@PostConstruct
		public void initialize() {
			this.pool = Executors.newFixedThreadPool(10);
		}

		@Override
		public ExecutorService getInstance() {
			return this.pool;
		}

		@PreDestroy
		public void shutdown() {
			this.pool.shutdown();
		}
	}


O vraptor-simplemail usa o vraptor-environment para gerenciar diferentes configurações de servidores
SMTP (para o ambiente de desenvolvimento, de produção, etc.). Então, nos seus arquivos .properties
específicos para cada ambiente (development.properties, production.properties, etc.), coloque as
configurações do seu servidor de envio de e-mail:

	vraptor.simplemail.main.server = localhost
	vraptor.simplemail.main.port = 25
	vraptor.simplemail.main.tls = false
	vraptor.simplemail.main.from = no-reply@myapp.com

Caso seja conveniente, configure ainda no arquivo de propriedades de cada ambiente a propriedade replyTo
do servidor de envio de e-mail:
	vraptor.simplemail.main.replyTo = support@myapp.com


# ajuda

Para maiores informações, consulte a lista de e-mails da comunidade VRaptor.
