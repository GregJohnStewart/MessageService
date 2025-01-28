package mil.army.dcgs.messageService.database;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Named("PriorityMessageRepository")
@ApplicationScoped
public class PriorityMessageRepository implements PanacheRepository<PriorityMessage> {
    
    

    public void populate() {
        //TODO:: from config
    }
    
    public List<PriorityMessage> getMessagesToDisplay(){
        //TODO: filter based on dates
        return this.findAll().stream().toList();
    }
}