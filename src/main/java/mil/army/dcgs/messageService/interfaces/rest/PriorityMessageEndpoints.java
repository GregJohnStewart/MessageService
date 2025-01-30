package mil.army.dcgs.messageService.interfaces.rest;

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
import mil.army.dcgs.messageService.database.PriorityMessage;
import mil.army.dcgs.messageService.database.PriorityMessageRepository;
import mil.army.dcgs.messageService.interfaces.RestInterface;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequestScoped
@Path("/api/messages")
@Authenticated //TODO:: use Roles
public class PriorityMessageEndpoints extends RestInterface {

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
            .title(priorityMessageJson.get("title").asText())
            .priority(priorityMessageJson.get("priority").asInt())
            .content(priorityMessageJson.get("content").asText())
                         .postingUser(this.getUserId())
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
        ObjectNode newPriorityMessageJson) {

        log.info("Updating a priority message: {}", newPriorityMessageJson);

        PriorityMessage message = this.priorityMessageRepository.find("id", id).firstResultOptional()
            .orElseThrow(NotFoundException::new);

        message.setTitle(newPriorityMessageJson.get("title").asText());
        message.setPriority(newPriorityMessageJson.get("priority").asInt());
//        message.setDate(newPriorityMessageJson.get("date").asText());
        message.setContent(newPriorityMessageJson.get("content").asText());
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
