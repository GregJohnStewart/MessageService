package mil.army.dcgs.landpage.interfaces.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mil.army.dcgs.landpage.interfaces.RestInterface;

import java.util.List;
import mil.army.dcgs.landpage.database.Timezone;
import mil.army.dcgs.landpage.database.TimezoneRepository;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Slf4j
@RequestScoped
@Path("/api/landpage/timezones")
@Authenticated //TODO:: use Roles
public class TimezoneEndpoints extends RestInterface {
 
    @Getter
    @Inject
    TimezoneRepository timezoneRepository;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public @NotNull List<String> getAllZoneIds() {
        return timezoneRepository.getAllZoneIds();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public @NotNull Timezone createTimezone(ObjectNode priorityMessageJson) {
        Timezone newTimezone;

        newTimezone = Timezone.builder()
                                        .identifier(priorityMessageJson.get("id").asText())
                                        .build();

        log.debug("New time zone (pre persist): {}", newTimezone);
        

        if(timezoneRepository.find("identifier", newTimezone.getIdentifier()).count() > 0){
            throw new IllegalArgumentException("The time zone is already in the list.");
        }
        else if(!timezoneRepository.isValidZoneId(newTimezone.getIdentifier())) {
            throw new IllegalArgumentException("Invalid time zone.");
        }
        
        this.getTimezoneRepository().persist(newTimezone);
        log.debug("Created new time zone!");
        return newTimezone;
    }
    
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public @NotNull Timezone deleteTimezone(@NotNull @PathParam("id") String identifier) throws UnsupportedEncodingException {
        
        // URLDecoder decodes "+" to a space, we need to manually change them back first.
        String plusEncodedId = identifier.replaceAll("\\+", "%2b");
        log.debug("Deleting a time zone: {}", URLDecoder.decode(plusEncodedId, "UTF-8"));
		
        Timezone timezone = this.timezoneRepository.find("identifier", URLDecoder.decode(plusEncodedId, "UTF-8")).firstResultOptional()
            .orElseThrow(NotFoundException::new);

        this.timezoneRepository.delete(timezone);

        return timezone;
    }
}
