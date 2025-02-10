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
import mil.army.dcgs.landpage.database.PriorityMessage;
import mil.army.dcgs.landpage.database.PriorityMessageRepository;
import mil.army.dcgs.landpage.interfaces.RestInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequestScoped
@Path("/api/landpage")
@Authenticated //TODO:: use Roles
public class PriorityMessageEndpoints extends RestInterface {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Getter
    @Inject
    PriorityMessageRepository priorityMessageRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public @NotNull List<PriorityMessage> getAllMessages() {
        List<PriorityMessage> output = priorityMessageRepository.listAll();

        return output;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public @NotNull PriorityMessage createMessage(ObjectNode priorityMessageJson) {
        PriorityMessage newMessage;

        newMessage = PriorityMessage.builder()
            .subject(priorityMessageJson.get("subject").asText())
            .priority(priorityMessageJson.get("priority").asInt())
            .content(priorityMessageJson.get("content").asText())
            .postingUser(this.getUserId())
            .startDate(LocalDateTime.parse(priorityMessageJson.get("startDate").asText(), dateFormatter))
            .endDate(LocalDateTime.parse(priorityMessageJson.get("endDate").asText(), dateFormatter))
            .lastUpdated(LocalDateTime.now())
            .build();

        log.debug("New priority message (pre persist): {}", newMessage);

        this.getPriorityMessageRepository().persist(newMessage);
        log.debug("Created new priority message!");
        return newMessage;
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public @NotNull PriorityMessage updateMessage(
        @NotNull @PathParam("id") UUID id,
        ObjectNode priorityMessageJson) {

        log.info("Updating a priority message: {}", priorityMessageJson);

        PriorityMessage message = this.priorityMessageRepository.find("id", id).firstResultOptional()
            .orElseThrow(NotFoundException::new);

        message.setSubject(priorityMessageJson.get("subject").asText());
        message.setPriority(priorityMessageJson.get("priority").asInt());
        message.setContent(priorityMessageJson.get("content").asText());
        message.setPostingUser(this.getUserId());
        message.setStartDate(LocalDateTime.parse(priorityMessageJson.get("startDate").asText(), dateFormatter));
        message.setEndDate(LocalDateTime.parse(priorityMessageJson.get("endDate").asText(), dateFormatter));
        message.setLastUpdated(LocalDateTime.now());
        message.persist();

        return message;
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public @NotNull PriorityMessage deleteMessage(@NotNull @PathParam("id") UUID id) {
        log.info("Deleting a priority message: {}", id);
		
        PriorityMessage message = this.priorityMessageRepository.find("id", id).firstResultOptional()
            .orElseThrow(NotFoundException::new);

        this.priorityMessageRepository.delete(message);

        return message;
    }
}
