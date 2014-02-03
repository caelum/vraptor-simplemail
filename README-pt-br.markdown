## vraptor-simplemail

Envie e-mails sem se preocupar com as configurações do servidor SMTP
ou com bloquear o processamento enquanto envia um e-mail.

# instalação

O vraptor-simplemail.jar pode ser baixado dos repositórios do Maven, ou
configurado em qualquer ferramenta compatível:

	<dependency>
		<groupId>br.com.caelum.vraptor</groupId>
		<artifactId>vraptor-simplemail</artifactId>
		<version>1.2.1</version>
		<scope>compile</scope>
	</dependency>

O vraptor-simplemail depende da biblioteca Commons Email, da Apache
(http://commons.apache.org/email/).

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
			Email email = new SimpleEmail();
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
			Email email = new SimpleEmail();
			email.setSubject("Your new password");
			email.addTo(user.getEmail());
			email.setMsg(user.generateNewPassword());
			mailer.asyncSend(email); // As configurações restantes são feitas pelo Mailer
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

Você também pode enviar e-mails num ambiente parecido com uma transação: envie
quantos e-mails você quiser dentro do seu controller e, se uma exceção ocorrer,
cancele esses e-mails. Para fazer isso, use o método `sendLater` do
`TemplateMailer`. O interceptor `AsyncMailerFlushInterceptor` é responsável por
enviar de fato ou cancelar os e-mails se necessários.

# templates

O vraptor-simplemail integra muito bem com o Freemarker. Você pode usar
templates do Freemarker para seus e-mails, assim você não precisa escrever o
corpo inteiro do e-mail dentro de uma classe Java.

Para usar o simplemail com o Freemarker, você também vai precisar do
vraptor-freemarker. Coloque seus templates dentro de um diretório chamado
`templates` e, para usá-los, peça por um `TemplateMailer` no construtor do seu
controlador.

	@Resource
	public class PasswordResetterController {

		private final User user;
		private final AsyncMailer mailer;
		private final TemplateMailer templates;

		public PasswordResetterController(User user, AsyncMailer mailer, TemplateMailer templates) {
			this.user = user;
			this.mailer = mailer;
			this.templates = templates;
		}

		// métodos do controlador
	}

Então, para criar e enviar um e-mail, especifique qual template você quer usar,
preencha as variáveis necessárias e, finalmente, especifique o destinatário da
mensagem. Neste último passo, você vai receber uma instância de `Email` pronta
para envio.

	@Path("/password/send")
	@Post
	public void sendNewPassword() {
		Email email = this.templates
				.template("forgotMail.ftl")
				.with("user", this.user)
				.with("password", this.user.generateNewPassword())
				.to(this.user.getName(), this.user.getEmail());
		mailer.asyncSend(email); // As configurações restantes são feitas pelo Mailer
	}


Você pode também utilizar uma configuração diferente para o freemarkerchamando o método `with(configuration)` antes do método `.template()`:

```java
@Path("/password/send")
@Post
public void sendNewPassword() {
	Configuration configuration = new Configuration();
	//configure 

	Email email = this.templates
			.with(configuration)
			.template("forgotMail.ftl")
			.with("user", this.user)
			.with("password", this.user.generateNewPassword())
			.to(this.user.getName(), this.user.getEmail());
	mailer.asyncSend(email); // Hostname, port and security settings are made by the Mailer
}
```

Obs: Essa configuração será lida a cada request. Ela NÃO É application scoped


# ambientes

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

No ambiente 'development', o vraptor-simplemail usará o MockMailer para simular o envio dos emails. Essa
implementação simplesmente loga o envio dos emails com o sl4j.

Em qualquer outro ambiente, o vraptor-simplemail usará o DefaultMailer ou a classe que pode
ser especificada com a propriedade `mailer.implementation` do arquivo .properties do vraptor-environment.

# amazon SES
Você pode enviar emails usando o serviço do Amazon SES
(http://aws.amazon.com/ses/).  Para usar o SES, você precisa configurar o
vraptor-simplemail para usar o mailer do SES no .properties do seu ambiente:

    mailer.implementation = br.com.caelum.vraptor.simplemail.aws.AmazonSESMailer

Esse mailer enviará emails reais somente no ambiente "production". No ambiente
"development", por exemplo, ele vai apenas logar os emails com o log4j.

Se você você precisa enviar emails reais em algum ambiente que não seja
"production", você pode configurar esse comportamento no arquivo properties do
seu ambiente. Por exemplo, se você precisa enviar emails reais no ambiente
"testing", as seguintes configurações precisam ser adicionadas no arquivo
testing.properties:

    mailer.implementation = br.com.caelum.vraptor.simplemail.aws.AmazonSESMailer
    vraptor.simplemail.send_real_email = true

# ajuda

Para maiores informações, consulte a lista de e-mails da comunidade VRaptor.
