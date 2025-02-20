package mil.army.dcgs.landpage.interfaces.ui;


import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import mil.army.dcgs.landpage.config.HelpfulLinksConfig;

@RequestScoped
@Path("/landpage")
@PermitAll
public class LandpageViewUi extends UiEndpoint {
	
	@Getter
	@Location("viewer")
	Template pageTemplate;

	@Getter
	@Inject
	HelpfulLinksConfig helpfulLinksConfig;

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public TemplateInstance get() {

		// Determine if the logged in use has the dcgsa_admin role.
		boolean isDcgsaAdmin = this.getAccessToken() != null && this.getAccessToken().getGroups() != null 
            ? this.getAccessToken().getGroups().contains("dcgsa_admin") 
            : false;

		return this.getDefaultPageSetup()
				   .data("userLoggedIn", this.getAccessToken().getRawToken() != null)
				   .data("helpfulLinks", helpfulLinksConfig.categories())
				   .data("isdcgsaadmin", isDcgsaAdmin);
	}
}
