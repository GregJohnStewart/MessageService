package mil.army.dcgs.messageService.interfaces.ui;


import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import mil.army.dcgs.messageService.interfaces.RestInterface;

import java.net.URI;

@RequestScoped
@Path("/")
@PermitAll
public class IndexUi extends RestInterface {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public Response get() {
		return Response.seeOther(URI.create("/messages"))
				   .build();
	}
}
