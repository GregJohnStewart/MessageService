package mil.army.dcgs.messageService.interfaces.ui;


import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;

@RequestScoped
@Path("/messageManage")
@Authenticated //TODO:: change to role
public class MessageEditUi extends UiEndpoint {
	
	@Getter
	@Location("editor")
	Template pageTemplate;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public TemplateInstance get() {
		return this.getDefaultPageSetup();
	}
}
