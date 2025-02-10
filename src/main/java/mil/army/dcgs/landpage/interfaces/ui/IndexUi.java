package mil.army.dcgs.landpage.interfaces.ui;


import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mil.army.dcgs.landpage.interfaces.RestInterface;

import java.net.URI;

@RequestScoped
@Path("/")
@PermitAll
public class IndexUi extends RestInterface {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public Response get() {
		return Response.seeOther(URI.create("/landpage"))
				   .build();
	}
}
