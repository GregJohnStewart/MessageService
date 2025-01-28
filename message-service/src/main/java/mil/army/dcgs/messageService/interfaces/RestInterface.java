package mil.army.dcgs.messageService.interfaces;


import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Slf4j
public abstract class RestInterface {
	
	@Getter(AccessLevel.PROTECTED)
	@Inject
	JsonWebToken accessToken;
	
	public String getUserId(){
		return this.getAccessToken().getClaim("upn");
	}
}
