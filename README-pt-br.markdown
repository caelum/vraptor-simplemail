## vraptor-freemarker

Uma biblioteca simples para renderizar templates freemarker de dentro de arquivos jar, email, etc.

# instalação

É possível fazer o download do Vraptor-freemarker.jar do repositório do Maven, ou configurado em qualquer ferramenta compatível:

		<dependency>
			<groupId>br.com.caelum.vraptor</groupId>
			<artifactId>vraptor-freemarker</artifactId>
			<version>1.0.0</version>
			<scope>compile</scope>
		</dependency>


# Renderizando páginas

@Resource
public class DashboardController {

	private final Usuario usuario;
	private final Freemarker freemarker;

	public DashboardController(Usuario usuario, Freemarker freemarker) {
		this.usuario = usuario;
		this.freemarker = freemarker;
	}
	
	@Path("/admin/dashboard")
	@Get
	public void lista() throws IOException, TemplateException {
		freemarker.use("dashboard").with("usuarioLogado", usuario).render();
	}
	
}

# Renderizando emails

String body = freemarker.use("notificacao_email_enviado").with("usuarioLogado", usuario).getContent();

# Ajuda

Receba assistência dos desenvolvedores do vraptor e da comunidade na lista de emails do vraptor.
