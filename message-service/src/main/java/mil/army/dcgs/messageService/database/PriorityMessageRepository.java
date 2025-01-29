package mil.army.dcgs.messageService.database;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import mil.army.dcgs.messageService.config.PriorityMessageConfig;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Named("PriorityMessageRepository")
@ApplicationScoped
public class PriorityMessageRepository implements PanacheRepository<PriorityMessage> {
    
    @Inject
    PriorityMessageConfig priorityMessageConfig;
    

    public void populate() {
        for(PriorityMessageConfig.Message curMessageConfig : this.priorityMessageConfig.messages()){
            PriorityMessage priorityMessage = PriorityMessage.builder()
                                                  .title(curMessageConfig.title())
                                                  .priority(curMessageConfig.priority())
                                                  .content(curMessageConfig.content())
                                                  .showDateStart(
                                                      curMessageConfig.showStartDate().orElse(LocalDateTime.now())
                                                  )
                                                  .postingUser("config")
                                                  .build();
            
            if(this.find("title", priorityMessage.getTitle()).count() > 0){
                log.info("Message already exists: {}", priorityMessage);
                continue;
            }
            this.persist(priorityMessage);
            log.info("Message created: {}", priorityMessage);
        }
        
    }
    
    public List<PriorityMessage> getMessagesToDisplay(){
        //TODO: filter based on dates
        return this.findAll().stream().toList();
    }
}