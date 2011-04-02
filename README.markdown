## vraptor-freemarker

A simple freemarker engine for rendering templates from within jar files, or rendering email etc.

# installing

Vraptor-freemarker.jar can be downloaded from mavens repository, or configured in any compatible tool:

		<dependency>
			<groupId>br.com.caelum.vraptor</groupId>
			<artifactId>vraptor-freemarker</artifactId>
			<version>1.0.0</version>
			<scope>compile</scope>
		</dependency>


# usage for rendering pages

@Resource
public class DashboardController {

	private final User user;
	private final Freemarker freemarker;

	public DashboardController(User user, Freemarker freemarker) {
		this.user = user;
		this.freemarker = freemarker;
	}
	
	@Path("/admin/dashboard")
	@Get
	public void list() throws IOException, TemplateException {
		freemarker.use("dashboard").with("currentUser", user).render();
	}
	
}

# usage for rendering emails

String body = freemarker.use("send_mail_notification").with("currentUser", user).getContent();

# help

Get help from vraptor developers and the community at vraptor mailing list.
