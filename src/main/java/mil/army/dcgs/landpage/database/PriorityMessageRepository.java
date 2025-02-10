package mil.army.dcgs.landpage.database;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import mil.army.dcgs.landpage.config.PriorityMessageConfig;

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
                                                  .subject(curMessageConfig.subject())
                                                  .priority(curMessageConfig.priority())
                                                  .content(curMessageConfig.content())
                                                  .postingUser(curMessageConfig.postingUser())
                                                  .startDate(curMessageConfig.startDate())
                                                  .endDate(curMessageConfig.endDate())
                                                  .lastUpdated(curMessageConfig.lastUpdated())
                                                  .build();
            
            if(this.find("subject", priorityMessage.getSubject()).count() > 0){
                log.info("Message already exists: {}", priorityMessage);
                continue;
            }
            this.persist(priorityMessage);
            log.info("Message created: {}", priorityMessage);
        }
        
    }
    
    public List<PriorityMessage> getMessagesToDisplay(){
        //TODO: filter/order based on dates
        return this.findAll().stream().toList();
    }
}